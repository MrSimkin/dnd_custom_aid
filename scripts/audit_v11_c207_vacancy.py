#!/usr/bin/env python3
"""Independent C207 detection of OMITTED handwriting reserves in real App PDFs.

Complement to the existing drawn-grid auditor: a grid is missing if a genuine
C2 paper module has >=128pt of unprotected unused BOTTOM height. A four-field
full-width Equipo Especial table is ONE region, not two independent columns.
Golden ZIP is inventoried (eight SHA256 and page roles), NOT declared matched.
No installed Android / owner or global G4 PASS.
"""
from __future__ import annotations
import argparse
import hashlib
import json
import zipfile
from pathlib import Path
import fitz

BOTTOM = 707.0
MIN_GAP = 128.0

def role(page):
    lines = page.get_text().splitlines()
    return lines[1] if len(lines) > 1 else ""

def grids(page):
    paths = page.get_drawings()
    h = [s["rect"] for s in paths if s["type"] == "s" and
         s.get("width") is not None and abs(s["width"]-.5)<.08 and
         s["rect"].height < .05 and s["rect"].width > 180]
    v = [s["rect"] for s in paths if s["type"] == "s" and
         s.get("width") is not None and abs(s["width"]-.5)<.08 and
         s["rect"].width < .05 and s["rect"].height > 46]
    out=[]
    for w in page.get_text("words"):
        if w[4] != "NOTAS" or abs(w[3]-w[1]-14.4)>.15:
            continue
        y=w[3]+4.6
        for line in h:
            if abs(line.y0-y)<.4 and line.x0<w[0]+2 and line.x1>w[2]+50:
                cols=[c for c in v if abs(c.y0-y)<.4 and
                      line.x0-1<=c.x0<=line.x1+1]
                if cols: out.append((line.x0,line.x1,y,max(c.y1 for c in cols)))
                break
    return out

def missing_by_side(page):
    words = page.get_text("words")
    valid_grids = grids(page)
    if role(page).startswith("C2 - Equipo especial"):
        # The approved four-field table spans the full page: its nominal
        # left/right fields are not independently available for NOTES.
        src=[w for w in words if 106<=w[1]<=720 and 42<=w[0] and w[2]<=570]
        bottom=max((w[3] for w in src),default=105.)
        gap=BOTTOM-bottom
        return {"sides":{"full-width":{"source_bottom":round(bottom,2),
                  "free_height":round(gap,2),"grid_protects":bool(valid_grids)}},
                "missing":["full-width"] if gap>=MIN_GAP and not valid_grids else [],
                "detected_grids":len(valid_grids)}
    sides={}
    missing=[]
    for left,right,name in ((42,300,"left"),(312,570,"right")):
        src=[w for w in words if 106<=w[1]<=720 and
             w[0]>=left and w[2]<=right and
             not (w[4]=="NOTAS" and abs(w[3]-w[1]-14.4)<.15)]
        bottom=max((w[3] for w in src),default=105.)
        gap=BOTTOM-bottom
        covered=any(x1>=left+100 and x0<=right-100 and
                    y0>=bottom-5 and y1>y0+46
                    for x0,x1,y0,y1 in valid_grids)
        sides[name]={"source_bottom":round(bottom,2),
                     "free_height":round(gap,2),"grid_protects":covered}
        if gap>=MIN_GAP and not covered:missing.append(name)
    return {"sides":sides,"missing":missing,
            "detected_grids":len(valid_grids)}

def audit(pdf_paths,minimum=48):
    if len(pdf_paths)<minimum:
        raise ValueError("Insufficient genuine App PDFs: "+str(len(pdf_paths)))
    files=[]; problems=[]; c2_pages=0; checked=0; protected=0; dense=0
    for path in pdf_paths:
        raw=path.read_bytes()
        doc=fitz.open(stream=raw,filetype="pdf")
        issues=[]; n_c2=0
        for i,page in enumerate(doc):
            section=role(page)
            if not section.startswith("C2 - "):continue
            n_c2+=1;c2_pages+=1
            state=missing_by_side(page)
            checked+=len(state["sides"])
            dense+=sum(x["free_height"]<MIN_GAP for x in state["sides"].values())
            protected+=sum(x["grid_protects"] for x in state["sides"].values())
            if state["missing"]:
                problem={"file":path.name,"physical_page":i+1,
                         "module":section,**state}
                problems.append(problem);issues.append(problem)
        files.append({"file":path.name,"sha256":hashlib.sha256(raw).hexdigest(),
                      "pages":len(doc),"c2_pages":n_c2,"issues":issues[:4]})
        doc.close()
    return {"result":"PASS_SCOPED" if not problems else "FAIL",
            "scope":"C207 missing usable BOTTOM grids; not all golden/Android/G4",
            "pdf_count":len(pdf_paths),"c2_pages":c2_pages,
            "inspected_regions":checked,"small_gaps":dense,
            "regions_covered_by_existing_grid":protected,
            "missing_reservations":len(problems),"errors":problems[:12],
            "pdfs":files}

def golden_inventory(path):
    with zipfile.ZipFile(path) as z:
        bad=z.testzip()
        if bad:raise ValueError("Corrupt approved ZIP: "+bad)
        names=sorted(n for n in z.namelist() if n.lower().endswith(".pdf"))
        if len(names)!=8:raise ValueError("Approved golden must contain 8 PDFs")
        cases=[]
        for name in names:
            raw=z.read(name)
            with fitz.open(stream=raw,filetype="pdf") as doc:
                pages=[i+1 for i,p in enumerate(doc)
                       if role(p).startswith("C2 - ")]
                cases.append({"name":name,
                              "sha256":hashlib.sha256(raw).hexdigest(),
                              "pages":len(doc),"c2_title_pages":pages})
        return {"scope":"Frozen eight-PDF inventory only; not visual G4 PASS",
                "cases":cases}

def main():
    p=argparse.ArgumentParser(description=__doc__)
    p.add_argument("--pdf-dir",type=Path,required=True)
    p.add_argument("--golden-zip",type=Path)
    p.add_argument("--minimum-pdfs",type=int,default=48)
    p.add_argument("--report",type=Path,required=True)
    a=p.parse_args()
    result=audit(sorted(a.pdf_dir.glob("V11-INTERNAL-PROOF-*.pdf")),
                 a.minimum_pdfs)
    if a.golden_zip:
        result["golden_inventory"]=golden_inventory(a.golden_zip)
    a.report.parent.mkdir(parents=True,exist_ok=True)
    a.report.write_text(json.dumps(result,indent=2,ensure_ascii=False)+"\n",
                        encoding="utf-8")
    print(json.dumps({k:v for k,v in result.items()
                      if k not in ("pdfs","golden_inventory")},ensure_ascii=False))
    return 0 if result["result"]=="PASS_SCOPED" else 1

if __name__=="__main__":
    raise SystemExit(main())

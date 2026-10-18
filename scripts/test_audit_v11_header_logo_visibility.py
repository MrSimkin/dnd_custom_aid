#!/usr/bin/env python3
"""Negative/positive synthetic controls for the physical V11 header ink guard."""
import fitz
from audit_v11_header_logo_visibility import signature, evaluate

with fitz.open() as pdf:
    page = pdf.new_page(width=612, height=792)
    negative = signature(page)
    assert negative["logo_dark_fraction"] == 0, negative
    assert not evaluate([negative], negative, 0.006)
    page.draw_rect(fitz.Rect(57, 36, 122, 51),
                   color=(0.12, 0.19, 0.34), fill=(0.12, 0.19, 0.34))
    page.insert_text((160, 44), "Mara de los Siete Umbrales", fontsize=11)
    positive = signature(page)
    assert positive["logo_dark_fraction"] > 0.01, positive
    assert positive["header_word_count"] >= 3, positive
    assert evaluate([positive], positive, 0.006)
    assert positive["logo_dark_fraction"] > negative["logo_dark_fraction"]
print("V11 logo visibility independent synthetic red/green: PASS")

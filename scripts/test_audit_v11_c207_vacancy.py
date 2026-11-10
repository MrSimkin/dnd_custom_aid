#!/usr/bin/env python3
"""Physical negative controls for the C207 omitted-handwriting detector."""
import unittest
import fitz
from audit_v11_c207_vacancy import missing_by_side

def sheet(title,left_long,right_long):
    doc=fitz.open()
    page=doc.new_page(width=612,height=792)
    page.insert_text((45,40),"Aldren Vale",fontsize=11)
    page.insert_text((45,60),title,fontsize=11)
    page.insert_text((50,136),"FUENTE IZQUIERDA",fontsize=11)
    page.insert_text((319,136),"FUENTE DERECHA",fontsize=11)
    if left_long:page.insert_text((52,700),"L-CIERRE",fontsize=11)
    if right_long:page.insert_text((320,700),"R-CIERRE",fontsize=11)
    return doc,page

class C207VacancyTest(unittest.TestCase):
    def check(self,title,left,right,expected):
        doc,page=sheet(title,left,right)
        try:self.assertEqual(missing_by_side(page)["missing"],expected)
        finally:doc.close()
    def test_missing_right(self):
        self.check("C2 - Equipo y tesoro / EQUIPO / 2",True,False,["right"])
    def test_both_pairs_full(self):
        self.check("C2 - Equipo y tesoro / EQUIPO / 2",True,True,[])
    def test_full_width_special_not_two_regions(self):
        self.check("C2 - Equipo especial / continuación / 3",False,True,[])
    def test_full_width_short(self):
        self.check("C2 - Equipo especial / continuación / 3",False,False,["full-width"])

if __name__=="__main__": unittest.main()

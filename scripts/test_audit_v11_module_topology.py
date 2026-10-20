#!/usr/bin/env python3
"""Red/green tests for the V11 printed role-order oracle."""
import unittest

from audit_v11_module_topology import classify, inspect_roles


class TopologyTests(unittest.TestCase):
    def test_valid_full_and_optional_paths(self):
        for titles in (
            ["P1 - Hero", "P2 - Inventory", "P3 - History", "NOTAS"],
            ["P1 - Hero", "P2 - Inventory", "P3 - History",
             "C2 - Recursos", "C2 - Equipo", "P4 - Conjuros",
             "C-N3 - Notas digitales", "C-N3 - Notas digitales / CONTINÚA: Fuente",
             "Índice del Libro de Conjuros", "Libro de conjuros", "NOTAS"],
        ):
            self.assertEqual(inspect_roles(titles), [])

    def test_rejects_mixed_or_misordered_modules(self):
        for titles in (
            ["P1 - Hero", "P2 - Inventory", "P3 - History",
             "C-N3 - Notas digitales", "C2 - Recursos", "NOTAS"],
            ["P1 - Hero", "P2 - Inventory", "P3 - History",
             "Libro de conjuros", "C-N3 - Notas digitales", "NOTAS"],
            ["P1 - Hero", "P2 - Inventory", "P3 - History",
             "NOTAS", "C2 - Recursos"],
            ["P1 - Hero", "P2 - Inventory", "P3 - History",
             "Unknown module", "NOTAS"],
            ["P1 - Hero", "P2 - Inventory", "P3 - History",
             "P4 - Conjuros", "P3 - Narrativa / continuación", "NOTAS"],
            ["P1 - Hero", "P2 - Inventory", "P3 - History",
             "P4 - Conjuros", "C2 - Equipo", "NOTAS"],

        ):
            self.assertTrue(inspect_roles(titles), titles)

    def test_frozen_owner_golden_extreme_places_c2_before_quick_spells(self):
        import zipfile
        from pathlib import Path
        import fitz
        frozen = Path(__file__).resolve().parents[1] / (
            "docs/pc-sheet-v11/dnd_custom_aid_V11_Carta_revision.zip"
        )
        with zipfile.ZipFile(frozen) as archive:
            for variant in ("A", "B"):
                raw = archive.read(
                    "V11_Carta/pdfs/v11_extremo_variante_" + variant + ".pdf"
                )
                with fitz.open(stream=raw, filetype="pdf") as pdf:
                    titles = [
                        page.get_text().splitlines()[3]
                        for page in pdf
                    ]
                c2 = [i for i, t in enumerate(titles)
                      if t == "CONTINUACIONES C2"]
                p4 = [i for i, t in enumerate(titles)
                      if t == "LISTA RÁPIDA DE CONJUROS"]
                self.assertTrue(c2 and p4)
                self.assertLess(max(c2), min(p4),
                    "Frozen golden V11 extreme requires C2 before P4")

    def test_title_classification_strict(self):
        self.assertEqual(classify("C-N3 - Notas digitales / CONTINÚA: nota"), "CN3")
        self.assertEqual(classify("Índice del Libro de Conjuros"), "BOOK")
        self.assertIsNone(classify("Digital notes"))
        self.assertIsNone(classify("NOTAS manuscritas"))


if __name__ == "__main__":
    unittest.main()

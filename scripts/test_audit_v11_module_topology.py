#!/usr/bin/env python3
"""Red/green tests for the V11 printed role-order oracle."""
import unittest

from audit_v11_module_topology import classify, inspect_roles


class TopologyTests(unittest.TestCase):
    def test_valid_full_and_optional_paths(self):
        for titles in (
            ["P1 - Hero", "P2 - Inventory", "P3 - History", "NOTAS"],
            ["P1 - Hero", "P2 - Inventory", "P3 - History",
             "P4 - Conjuros", "C2 - Recursos", "C2 - Equipo",
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
             "P3 - Narrativa / continuación", "NOTAS"],
        ):
            self.assertTrue(inspect_roles(titles), titles)

    def test_title_classification_strict(self):
        self.assertEqual(classify("C-N3 - Notas digitales / CONTINÚA: nota"), "CN3")
        self.assertEqual(classify("Índice del Libro de Conjuros"), "BOOK")
        self.assertIsNone(classify("Digital notes"))
        self.assertIsNone(classify("NOTAS manuscritas"))


if __name__ == "__main__":
    unittest.main()

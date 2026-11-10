#!/usr/bin/env python3
"""Negative controls for independent V11 physical-envelope audit."""
import importlib.util
import tempfile
import unittest
from pathlib import Path

import fitz

SPEC = importlib.util.spec_from_file_location(
    "v11_audit", Path(__file__).with_name("audit_v11_pdf_physical_envelope.py")
)
audit = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(audit)


class PhysicalEnvelopeNegativeControls(unittest.TestCase):
    def test_rejects_non_letter_and_missing_source_sheets(self):
        with tempfile.TemporaryDirectory() as directory:
            out = Path(directory) / "V11-INTERNAL-PROOF-bad.pdf"
            doc = fitz.open()
            page = doc.new_page(width=600, height=800)
            page.insert_text((20, 20), "OUTSIDE SAFE BOUNDS")
            doc.save(out)
            doc.close()
            result = audit.inspect_one(out)
            self.assertEqual(result["status"], "FAIL")
            self.assertTrue(any("non-Letter" in issue for issue in result["errors"]))
            self.assertTrue(any("misordered P1" in issue for issue in result["errors"]))


if __name__ == "__main__":
    unittest.main()

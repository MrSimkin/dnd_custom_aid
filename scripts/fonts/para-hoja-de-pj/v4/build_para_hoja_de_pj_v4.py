#!/usr/bin/env python3
"""Deterministically rebuild Para Hoja de PJ Symbols v4 from canonical TTX source.

Requires fonttools. The embedded source is gzip-compressed TTX, not the compiled TTF.
Use --emit-ttx to materialize the human-readable XML source.
"""
from __future__ import annotations

import argparse
import base64
import gzip
import hashlib
import tempfile
from pathlib import Path

from fontTools.ttLib import TTFont

EXPECTED_TTF_SHA256 = "2c49b21c0616bab315f2e6f6b8c7be55e835fb5fa1db4e17e248861d4794b3e2"
EXPECTED_TTF_GIT_BLOB_SHA1 = "cfc04ea4fb0ce53e8ee41c72df7e6281ef329c4a"
EXPECTED_TTX_SHA256 = "951c98add01ef66747be23535a946d25fe44a820087f8bd8c525e2df5219849c"
SOURCE_V1_SHA256 = "d0c0d50ad8ed33064e3af89b0976933b9eba8ead2d26234011c1a3233c246658"

SOURCE_TTX_GZIP_B64 = (
    'H4sIAAAAAAAC/+1d73LcNpL/7qfg+ovvqlY2QYAgmSjZUmzZ1q5lay3FyW5dVYqawUhcj4azQ44t5a3uGe7FDvwDDIdDNJozIzn2MrWbSGLjh0aj0Wg0GsDh'
    'X25vps4nsciSdPbDY/LUfeyI2SgdJ7OrHx7/fPHyIHz8lx8fHeb5y3SWO9lkln9QxP9z67ry/6T+ryyZ52+SS/2dPeX08Y+PHjnO4avp3fz63WIsFj/KX+Uf'
    '/nRw4FxcC+dJMn7ixHm+SC6XuXCSzEln0ztnki6c6+VNPMu+d5K8+HNyNUsXYux8vhYzZx4vMjF+6hwc1HAl/skLJxn/8FjyMYtvxA+Pn87SfCwmj591EBFF'
    'lM3jkegk8RTJVFzFo7vfRsliNBW/pct8msy6i9DuIpNkOhXjzhKsVSL79zJewJX43UWASnirRPopnoJVBF0FgArCFv04iW/S2RisIzKUAaohbquMVJt4dmXp'
    'FEJMpaCq2r1/LW7jq3QG10QNhaCKNjQgjxdwLX5XCaiKdv/PkzlcQ9BRAKqg3f+jazH62E260e3p8lJ2hbmE1+70y/RWjKECpLOAvaKNAb9Is6yblBpaUY73'
    '7iLtfpaddiXybtp2D18up9NM3BmQdffepNLAzjCGygu6ywCd7IWtIghT5UXdZYBqqNsqYjVWlHSVgKrwWgUw5opSQyGoItYqg7JX1DeVgqpqKwHGXtHAUAiq'
    'aEMNbPaKRl0loDmxrQI2e8VIRwGogrYCmI0C2+h2mxlh7U632SvmdxawV7Qx7o32igWGVpjtFWv3M2CvWLuH1+3V4bOm+1f84VrE44YjeJpmuZNOnPxaOnp5'
    'LDlzPsvecy6FsxCjeDpaTuNc+n6Xd5JEOKP0Zp5MxWLl/pVlar/TkaN/KSpXVjE7kb7re/EpaX5nje+lkM+XN0fjfy2z/EZIT7emcm8n5NJ1Scg18U18lYze'
    'Lm8uJQeayp+4Ezqa+Ksqp/FVpr/X/zj1f4kmW86SPDsTi+Mbzbf8vmJsIcqW199eLhLnXMwdEkqk78r/OZ7rNXiTjvsk6VHg9jTREolW3N81/uzJqWtFHt9q'
    'RgPfaxRofAgbJW7i0Xl+J3vUIIpVW6fpZ5HJbhqdnR2fKnK61ocvEqkPuezE18mqi1ZMJLOxuL1I36Sjl+niJl51oqa4KhTxRZzHnQSHzyrFLFVU/ghol3tb'
    'dOMa/3E2aigOiRqfxmLt2wGRo0s3W1q1V/F8k9d4/CmejcQvyTi/bko3chvSTWZvxCQ/T8biJxEv5KJtE0fSvE+urruIqLvetce3ebMJvKmJct7Oz6fpXLxP'
    'Mt2bpOv7crbJRPn53WSSiY5eWYhMLD6JsWv+RMyfPPMn2iENIafTkVEFJMWsHNrvJq9PS9KspYpSSUrVeFQq9+38vu2Ye7uuZpK90pxqvvxG4+Lbs1QODf0t'
    'bA7E2+dyCKXLxark+sebeZoluVhHcDtp2khrVP9MZyLbHJ3yy4UUR6GKQBXnebqIr0Tnt5fLWTn6X4hJd+GTWZYvljDNeR6PPh5PRWHlDRTJ7+LdpIEFCGMm'
    'UUAwTfVCzPPrDYNTaVDx47vz37xWcERa8uk4c54ss5fJIsufX8eLk8LEPXHi2bj485u4+deycPlPP637tK5wDZNw9OmqgC9NUKf9WWa/iKJDn0/jTLefNbRV'
    'EhSF1743Jsns4m6OmBruzpeX2WiRzPNfi87RrFBKOmj+sUbDKO3CMdmiBsw6iddwASTRXArNzpGmAnlaYQFcraDWiaKwiSUt1kchfeW16lyvg+KsGMiNbvfp'
    'qrrsZXyTTO/Wem31dR7P0kz8WKvb4WVF3OxJ3cPF53OxSCbrPkDzc6VAnZ/OFuk8XTSZXPtcGKGFHAGdH2U704/ig5zuYiPA0eLGzNcbkediUUwSnZ9Pk3Ex'
    'c3d++/V1d5sOnzVFd7ic/jxLRtJjfi+Xd4IYh4EDuI9rEF4DgsAQrgGC4rkwQbBtIOLR9QcxG8sFRV341em507AT52JaeX4b4IS0uWlZSz1/ew2SNcup1Tak'
    '0Uq2hW1KjwrHbbzy9NfcupLihWiRrHl3Jckbk4tXWMfZkdlvLL+/WHcem+jL6XMp9TM5X+6iQmsY3ja9l92aND57Hs/Xv615lctMztPxclr2RZd8fpILoI/N'
    'j2u9eFr5NOJ205OvptPSk7/Jb9V8nN+29gbkVCmnp3pWc6bZ5dr0rck7Qo/blFxb0/cEUEvqfsW2qWo9FrhVWRWt6Vm4DGD0K9MKw21XeDt2d+jN9fhLv7Kt'
    'IN12hbdrcTOyukXJ7SptBPP6F9yuyvUw9VZlt6y4ERrdouR2ldaRvZ6FWkHpLUv35LgjpLpNyW1Gbju02a/YNlVtZYe794B6Fu5jh7u3Q7YrvB27O/TmVna4'
    'e7Nku8LbtXgLO9y1m9avaH873LF906/kVna4e3uyZ+H+drhrO6xf0X522LA5uGXpnhxXWTOKOPK9ddrDZ5XTXfw4uonnneFNlSbUCBJL0t8mZVj2N+bMp3Fe'
    '/HLyoszpKX49no2K36isTbK9lGuW4pNeeMviTrEMLYKmq7QBleFThtXOz46eH+vQV7sMsWyTlRDHvz5/c3R6dHHy7q1zevT+b0Y0at4VK4He/nz60/F75/zk'
    '1VsTBiWWRIMS6MXJq5ML591bY8OoZ0o+aJS/+OWdsTw1bQY2y79+f2zkgLnmlIYS4/m709Pj989Pjt44RxdGFGLLySqx3sjOees8Pzo7uZBwb44vLqSYj4yg'
    'ni0Hywz6kxGUwjlXZsjnRkgGZmWZEV8YEW2pZGZMc0dzSw6cGfOlETPYms9XRsxwS2m+NiJaM9rMoCdG0NiavGZG/asR9dKWqGYGNdo7NgLz0syIb4yIYygP'
    'zQx4agQUlrQnM6bRRLOJJcfJjGm0t74LpjSZEc+MiMSWvmQG/bsR1LNmKplR3xtRqS0ryQx6bgRlYBKSGdE4F/k+lHRkBvzZCMjh1Egz5AcjZABnL5khfzFC'
    'hpjsSjPwr0bgCJMGZQb+hxE4NuRJmbH+acLifRyQ89OjN3b3g/dxP9Ygjc4Hxzsfa4BG14OjXY81PKPjwXs4HmuIRreD8207xuh18GDbjjE6HTzcrmOMPgeP'
    'rJn3Jkyjy8Fje5a9CdTocfBLaz69CdPocPARnDpvAjT6G3wMJsqb8IzuBhe2zGwTpNHb4BNbFrYJ0uhsBJaMaxOg0dcIiDW/2oRpdDUCz55KbQI1ehoBtSZN'
    'mzCNjkbA4PxoE6DRzwh8MBvahGd0MwJuOKxhQjJ6F0FgSKM2IRmdiiAEz3iY8Iy+RBCBOdgmPKMLEcSGoyEmJKMDIYptc4xR+Iv8BwAhmIWMBcPDRLssGBRl'
    'jiwgDLOCsmD4kC5aynLIQbSUDXaoN8TZSQtKhFrZWUBixBxggbi0r1ktCCPEagWEIKbB1YMN4pKdxwXZGFtbjAuyMbj6CILtbmXI5qDaQpp8H4wEe2Ak3EGa'
    '0a6a6W1qZo+yZPfq96CQ3oZCdkyrFgSGXuRbgPzd1coz6WYPtfI2VLPDPbMghNZAkgUgsse2LAgxuL9jKXxp2eeyFB9tPftS10Wt+i0gBLNjYcHABXEsIBQT'
    'FbFgMESEwQLh24M9FgSOi0pYUALUfooFJEQGMywwEW4bxoIS44IgFhTc5o0FBBM7sUCM7fs9FgRhj7dYECbWHSIYYONKCfwcRAlBBOwtEJ594WtBoOh4vAWI'
    'QWtcS1kfk6ZgweBgjoKlcGBJLwCLs32sydke1uRsH24aMy3KezHCdl0Qsg0Xre88wjb8s23mEbbhoW0xj7ANJ22reYRtuGrbzCNsw13bZh5hG37bFvMI2/De'
    'es8jbDWPbOm+Mlfs6IMzd7KbD87I9ss7RnZe3jHi7bgyY4TuZ2XGCNvan2fE3zn0wgjffi3DSNBnLXP4bC1j0ZrFSNeyGMmQxThkMQ5ZjEMW45DFOGQxDlmM'
    'QxbjkMU4ZDEOWYxDFuOQxThkMQ5ZjEMW45DFOGQxDlmMQxbjkMU4ZDEOWYxDFuOQxThkMQ5ZjEMW45DFOGQxDlmMQxbjkMU4ZDEOWYxDFuOQxThkMQ5ZjEMW'
    '4/6yGKs/Vfc3TtNR3H60tfjbk9YLN9aXRg6fVVCPqneYJuVPDdTiKao/O3flv8t3popHTlbvSmX9njepHoetari4KF/MaV/9XlRYPG8llf6u+rHIWCmqLm6p'
    'D9njsvbioavAX6Vnjqqnb37U7BzOc+dW4dQg5Vu6qychNFENi6UqazaSqRo7qWQXrjNqZJz4vKqNBhBPiopwH+CJMrcHVVeNeMZZWKH4IcC3r4gI1Djm1Y1j'
    'DGCbUEXmk50krqoDOde1gawrIcCcKykgGU8abx/pt0vqUdQ9qLoeSKjGFynyq+rxVTSoHl9B9efy5+K9OISaetXIKUCMMgt6UZU1AyoPUOF7mwYVDGNAXYqI'
    'cBfkiPYi66qyB+O0VhqXQgoYVFRhBBAFpIYiFByGSk0DFzSPrtJmhqgzgAaZV7cxct37HxfrF5UPw+PrHh6s2J8vdbrIJjRbXl4PoiJ51airRU5VQeVRiHFa'
    'ZJWW7SvSuszDiKlpCpoTVJ0RB9gvl+EV+9EeZBVGkV1UQUQRXAc+RlDlWtUuKC8M7FUGEJYSVOiSezUk+vmJPZsPPeZdnGmgyr9zO0cYqfXZh2YFKXbaMiFd'
    'aBHKHPHi3JMVi6rhCDOGbCVKZDUVV1oNscUgU6Ja6GGEBVt4JXgQSvciyJVWCbCBsH71WaxQnNBrsiCEGkh5zTqBsEjUmi4guYPTWEAQSD5DMKUmRLB9etYE'
    'ZaWpPI7gijCKaOBqAgYHNDxNK7nDYLoTYc5WigM2E9avHnpKI5SearJyG9/IlcdrQ0kpNO0RqpaxHCN+TdXZlYGLwOKMIBjzCaaVmgoUmaIKC4W0sRUQam+h'
    'HyKEpYlAwYNQuhdBrrRKgA2E9auHnnqEY/RUkUUEmhAlVxXrFHJPXV53DnftctdEXVAho3akoDinYWOKh6G9fYoIlpWmIpTauZKSJfYGygk7RAxoTQXKHQbT'
    'nQhztlIcsJmwfvXQU1eFBmA9XZEFAcCVq+RKCGRPy4TcaonnIcSvqTq7MooQWKHqI5CxUMUhwVauqCCRKaooIHa2IkrtLQwjbheWJgIFD0LpXgS50irR1cA9'
    'rszWojqRr1ZlPGruKbirPQXmIvYUfKXNYAi4jqaHHhQNU5sFBAyE+ypQFzB4gwIVgfP8emUAEEV1T/v3GoBrvaU4LJ6HxfO3u3jedZToxwCHYTIMkyHGNMSY'
    'hhjTl4wxbW/Oq3ec6zyXQFtxylZWnNOVFQ9chBX36q70IdVhdU9SSFPrukuBQGRMCT9wdxrWCgfHFKp9nSxtiKqzfXvs5/bb219k2o76bRqjsDzvQeT2Zf2d'
    'Lyc4E+OoeQZn+JFm+KEsYlcGhldl+pWLdXe1WPe95mKd27u7zDYvV9gBEGj01c6060VQaFqF6nxwm7vYtr6rGYRUx0U4pGUWYREn8QD2PZWZ6HbEunrkA9Sy'
    'CllkF1XgErukQK5XgmIkwgiqjNTbBMWhJCUlqOBhFLpKLR4WbMOCbViwDQu2YcH2B00K4CEqKUCREXD7w2O12aIEGopkldvuYpICwIj+KtscwOJ+iGDMpxzR'
    'Sk0FJwXUVCG0t6bYCiiihdxFbLhqIlDwIJTuRZArrRJgA2H96pUU4OKSAuqdNRKA+8lsPZ+he5fOj+qtfI5JCuDQnrlvR1K5HyBTPGL29ikiS1KAoiKU2Lla'
    '5clADZQTtodKCsDIHQZbZRaBnK0UB2wmrF979I5bZ0E33WNasNnhHgeyx/fmHlNFFhBwYYihwrmXCotSguCri+oeOsEcZRl6Ad0LZpNdu2K+Bxk0jiDCOYgK'
    'SooCNAgMRfZQ8Z/m4f1aD2n3YtnvfwAHufLDLSNpgFqTlrdq7WmxHLiYhbfvYVbxFNVKihIZxawlFVvgslS1cB+LZRex7ta9CC+WfdRi2fcfcIy0jPUwSIZB'
    '8i0OEvNKHRdRoqiIklfH6CwRJTfaV0SpzqkDkXgd7QOZ8jERJR/lMPioiJLiyhJRqhu4n4hSLXdLREl1oiWiRHERJfpADk/jBpjalPNQm/LAX5lyz1uZci/C'
    'mPKqhD1IoMgIhYJZRGe6wxnZ6haKgkfE2jeCMp/VXRUgVqCz5iHGKqnaWqmpLAcIvDrs4NnZCsGM7LqFZUdbgyo+QwgehNK9CHKlVQJsIKxfex4iLXdnGCPD'
    'GPkWx4hRT4nykGE9VWQRh5hy/aBmHdprd0kdFg58xGkR6LR/efOIDSlkCKbC2vUF2xfqKxHAYzWaKuR2rgiBskFUAwkJKOYYUkARcofBdCfCnDUUp6OZezTS'
    '65c//qfdnbKr2O4rwW24c+ae7pzZvsMbt06q3o6IDrQXmWmqt4tDyH16GxkfUUfWytvEjeKIlMMQECi3pc5Ig7aRWe0JUCjrTm/YhvAtcAgoyXJk56oWr62F'
    'hPjMLK5960XbDAyK8c0ohvnctDp27MEbMK7e34WcBxLWTWSgIKKqiT6UvKpdasi144TYkQLi25lirotoYKgOfIPSAgNLexy09Z2vQ1bnkNU5ZHUOWZ1DVudw'
    '1dNw1dNw1dNw1dNw1dM3cNXT9m5x67URdY6vzGSt9m8a/jEvs2X3vpz16rxZDo4lXqfq8geSSGuF//WLxHyYu14fB5AnwOqYA0h076u5jsdMVP/QVQ6s2+if'
    'oJEDyxD9Q9VxezgEyTFUPGhRuRAVIxgswimKLGCgv8bbZN1vS2CoPIqpUlOB/CsqUBaKqEusPc7N0qBepkESqANfzAdXx7WzA11qrYikq4yigvaIFesrKsNl'
    'Dygy38PU2SCjEQINFIamAuWqG9DVRT12KLg+IYF4/CNyd7/5TdlKqetguESR+Zg6Q/CINL/ftxfMT0AN5ncwv4P5HcyvuaN1xgWkeb6+DAM8M6vv8gAH/url'
    'LNhM63A7dPZU34rBgb02poMwzN2DrCI3tIsqJAzBdRBiBEXBOI0WFHj+UVUZUG4XVBfz+5yoWm977Gt6Whl4OPCjyajngSOMqEENJrD5LsLE68UXOF1wtRkK'
    'YjEVQQEZ05MK2EoPtfegqAKP29nywdzIuoUUE0yHPQgleNiDUL0IcqVVAmwgrF99Nnw85C6b2hCBYlvlft5dvZUGbGiH9klFb/j44H4ItyP5LLIzpSZ0sH0U'
    'lSarqTwobKq4Igy6REk1EJ7z9YCGHQgld4sDoToR5mylOGAzYf3qs+HDcNd4KDICKurKIMFbZEr8zENd4wFeBq6kD2Kt5gMXPOXMEa3UVJYT0/U1HhzBFjhj'
    'qBZyD3ONhxciBA9C6V4EudIqATYQ1q8+Gz5eiDt9oDZEXDBtPahvmQezunl9mwlDyF0Tde6H0NCOFPgIpnjA7e1TRJYNn0DvhCC4Ih6YbE7VLTkuZkBT8ElM'
    'JXcYTHcizNlKccBmwvrVJ0dNLTotb3tosgA8fkDV2x4u/LaHXuJFmKR6aAUb6asfIaxQLQRBxkKKaeWKCjyEUFNFAbezFTFqb2HkIoQVget4JXgQSvciyJVW'
    'ia4G7nFltv5ia6QfDA+aj3uw1X2hxYMX9qihqx/3ANSBUdd+AmYV4oVjt1QdmwUz4rhaYPs+IsQbgFjRg4R4O1/3GJbPw/L5W1w+7zpK2jkCwzAZhskQZRqi'
    'TEOU6YtEmbY3583XPRoXNDHDBU0ck+mlrhECN+z0HhvuDid4W09vTO6aEEb7MIVqH+GYG5c627fHfu5+3eOBp23cDIqbz6AJ5h7k9mX9nS8nOOvrHpgkBmTj'
    'kNkC92wRu3J8aGHS6xTY0F8ZxnKaUQdeQ8TrHkTdhwCdpPD1k6vQwxaByqQpM2YBy6h8W/BAqa9mTA6dbFR1RtBB10pYZexpp4yAWlYhj+yiAq9U0VxD73Gs'
    'BMU8ihEUBVMn1EGx0LcLquvw6j0o9NrrHsOCbViwDQu2YcE2LNj+aGkBHup8nSYjQQSeplJbdSF8AVNNRiPMsTFN1X2tJgaLhxjG/CBEtFJTWa4ODdf9CYit'
    'APKpVAu5j9hy5dD2hxY8CKV7EeRKqwTYQFi/ep0DxaUFEJUWAN9mV+9PQQmUxOXq/jxMWgB0B0HIEEhBfREfyBQPub19ish2DpS30oUhrqRkPXsDiRe5qNsb'
    'XYTcYTDdiTBnK8UBmwnr1x694+7XPRruMWs859h0jzkL9+ceU30PApTN4kcYKpx7qbAYmBGi+GL3u/VqeN1j6IVtesFssuvgpg/t73OOIMI5iFwfAwFDDx5F'
    'kT1U/KfjdQ/WvVhmvRfLDKeHDLdYpvpFVnCxTHiwt8VyQDFY+kgLyBhDLZYZahwxzGJZsQUuS1UL97BYVoKHF8uqF+HFMkMtllnwgGOkZayHQTIMkm9xkAAr'
    'dZxTgYooefWFypaIUn2z4B4iSvWNjiASrxPvQKZ8TETJRzkMPiqipLiyRJTUlZV7iSipGx3hiJLqREtEieIiSvSBHJ7N1z2ClSkPG6acNEy5hzLlBGfKNRmB'
    'b4ty9VEYcLmq70BCmXIPMuWhvgIJwgoijmAsoJhWBihTrqgi8MqoSAWEQnsLQ4wpD0FTrgQPQuleBLnSKtHVwD3rfsuPGZR/UP6vSvmBW5tRDzArsohDDzC7'
    '9T5OBE5tbv0MVhgGCIGGAfRchW9HCustHJCpUN3sDLVPEVmOwmiqkNi5IgTym1QDpQeMeYCZcA8hdxhMdyLMWUNxOpq5R+vb+R7HcJ/OcJ/OvajZPaXwDXo2'
    '3Ns03Nv0Nd7btL1B2Xwfx+M6Q5QVg0BZk1LwPawJMsLoKy8fysTwVHd6DNrBrvMewTfNfE89K+thnkGBdvFp4NuhiBcwxPNvnGJaKJccxCyufetFe5oZFOOb'
    'UQzg5UN1BTaD/GoW6fdx4JfG6/PoIJhXpyr5UFbzKhsG6miCQOIRgilGXEQD9cPLoLTA0OweB+36+zhDXvSQFz3kRQ950UNe9HBd2nBd2nBd2nBd2nBd2td8'
    'Xdr2bnH3+zi0rL46CcsbVwQE/D6Ws7RuLOegb1IrSBfVfUiktcL/+kViTsVjtWUBLzrwQzvRva/msnk8EhLy8E8HB04BF0sMZ5Y6dW854ziPnYODsvThs6vp'
    '3aT6sShe11D8uBCjdDEuQU9eFGPQmU/jfJIubopfSfXr8WxUf5xKlSh/vJW/LGfJSOrKD48vFkuhO/x5Or9bJFfXufNfo/925Mo4OPBcjzuvllkef0qd0+X/'
    '/W/6+1PnaDp1SrrMWYhMLD6J8ffyp3maJXm6uHPmy8tpMooLmTyTKplM6l+ceJlfp4vkdzF2JJ/Oi9kL57nETm+co2T8tBbeqm3mxpLdG3sWL2Lndfqv2BkL'
    '5+yvzvndzWU6zZxPrAcf3u58vBdXy2m86FEp3b3SukdPl7P09+8AUXznsKeu+51TqMGBGx2QsAef7A/SSf7ufHwQi6xQ4EIa3zuVmS3VeCWY751M2oWRcD4R'
    '5/z1kedzZ+yO3LHvxuNQjCl1ORM0noTRpRsFPKL0MhKXcSjisTf2uEeZS8iIxNSjdOQxzv0+sub7kXUh6rE4+2st5w/soL96hntTz9rgOM9alqIHM9G+mfle'
    '6p5WgNqqZVmS5VIZLu+cd3MxOzrpY8n2YLdPV/qYlR0nress1yZ2dC17dpSLhZNdC5FnT4smzGvLnTn5tXBuRDxLZleZk06c6pE95+jg+Fl8IJzP18lUSOs+'
    'XsSfJYn6HM9kG8TnusLMSWZyLpMNS2ZxIYqK6mAhJnJGG5fUldAORlNZlzOJb5JpIrJeRn8Phu/d55lYHFTzUDkLzfKnzlk5XxlmsJL3+SL9lxjlzvpsthDN'
    'GU32fktvN+e474t5Pr9OFuODebzI7xxZi5hlwkmkBG/mUiIljqTIZJ/kceEK9BIR/4PY3CJQ//AzI9nDcD+KnZ8unecj58XYORbO5KVz9cq5fu0kJ87srZO+'
    'c+Znzr//7nz64Hz+xbn91bn7Rw8GW4OdrvFHmvwxN/r6/TJk++7DFUNWvbP3haznSzhcDyl9f6uqvza3qodE78GT6qdq+3eetqr/vv0lJFP/oS4SUjr/YV7R'
    'QxrGtiN0b/MS2W607sndqX6v4lRSX/Kaz4KZOL+4m0v7HU+X0tfynupg8WGSx7Knj4pQpfrsNj4vZ3I0FBGxs0IBC91SRJskF9fJ6ONMZNkmTZK9TG7F+CzJ'
    'R9ebX2+S2am4KThkXsfX+Bb6qssSqGjHx3n2Voor01HEIhR4Uaj3pLAky0zaHOmd/nspnCsdOcycyziTI0HKobBHyawSrpSLDmHKfyaplElhagqaJ0VXPHHy'
    '+HIqnjrnyUzOo/LvcmRVgDelUzlLc+dS1BX+uQn2WTjX8SdZJpWIn4Q0mHJ4FkM9iac1RGHUJFuFVRxNY2k9pe08mTlSLaQxzdMmmqyjYKRAW4gi6lnyKO3E'
    'lbSI02aD/lzUPBOFVSyraQHNs+qvN/F8XtjbwqJIgyO5KFjJpD2WmlmA3znSME3E4qkUbpw/Kb7EeRPpLl06mRCStWn6+WnzSxmArTR9rbMOxW2+iDd7b5JO'
    'JUTBTVzVXTMuKyz/Ugg5jz8KadcX0uoVBNIqzsaxHMKn8aju50psqmqtKHXkuJox2hfbP8MQ13sjMG0r/RxFjAJeO4WPIEWBti+8xVGjoDc2UJDkKPD2FTI4'
    'alwHNtM6EaQo0ObJQzslCrK6stbSYc3bbWHS6slrPGUP6OrWcRyn1bWlFlWpcvgsXKqXUQ1k3Q9coIjh7uk+hoIiRgFj7EDH5TYwJdIOdF8vDhNj7YBhJxqm'
    'RtqB7tu/LH2CsAMdJwRgSoQd2Dynb1FKaAR23XINkyLswBplD2jQDnReX2xRFdAOtF9I1hvL686HdE1KV7v48SrO5jVV8eN7qYhySVr8+zS+PTs7Pi2ySMsX'
    'sco/vpI0Pwnp3CXpolxUP6sAK5hHh8/y/KVcBf746P8Bh8Mi5zJfAQA='
)


def _ttx_bytes() -> bytes:
    data = gzip.decompress(base64.b64decode(SOURCE_TTX_GZIP_B64))
    actual = hashlib.sha256(data).hexdigest()
    if actual != EXPECTED_TTX_SHA256:
        raise RuntimeError(f"embedded TTX SHA-256 mismatch: {actual}")
    return data


def _git_blob_sha1(data: bytes) -> str:
    h = hashlib.sha1()
    h.update(f"blob {len(data)}\0".encode("ascii"))
    h.update(data)
    return h.hexdigest()


def build(output: Path, emit_ttx: Path | None = None) -> None:
    ttx = _ttx_bytes()
    if emit_ttx is not None:
        emit_ttx.parent.mkdir(parents=True, exist_ok=True)
        emit_ttx.write_bytes(ttx)

    with tempfile.TemporaryDirectory() as tmp:
        ttx_path = Path(tmp) / "Para_Hoja_de_PJ_Symbols_v4.ttx"
        ttx_path.write_bytes(ttx)
        font = TTFont(recalcTimestamp=False)
        font.importXML(ttx_path)
        font.recalcTimestamp = False
        output.parent.mkdir(parents=True, exist_ok=True)
        font.save(output, reorderTables=True)

    built = output.read_bytes()
    sha256 = hashlib.sha256(built).hexdigest()
    git_sha = _git_blob_sha1(built)
    if sha256 != EXPECTED_TTF_SHA256 or git_sha != EXPECTED_TTF_GIT_BLOB_SHA1:
        output.unlink(missing_ok=True)
        raise RuntimeError(
            f"non-deterministic v4 build: sha256={sha256} git_blob={git_sha}"
        )


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("output", type=Path)
    parser.add_argument("--emit-ttx", type=Path, default=None)
    args = parser.parse_args()
    build(args.output, args.emit_ttx)
    print(args.output)
    print(f"sha256={EXPECTED_TTF_SHA256}")
    print(f"git_blob_sha1={EXPECTED_TTF_GIT_BLOB_SHA1}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

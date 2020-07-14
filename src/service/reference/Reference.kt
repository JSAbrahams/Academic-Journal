package service.reference

import service.Notable

class Reference(start: Int, end: Int, note: String?, val bibTex: Bibtex) : Notable(start, end, note)

package main.kotlin.model

import javafx.scene.paint.Color
import main.kotlin.model.Notable

class Highlight(start: Int, end: Int, note: String?, val color: Color) : Notable(start, end, note)

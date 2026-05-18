package com.smartwordgame.app.data

/**
 * Strips Hebrew niqqud (vowel marks) from a string for sorting purposes.
 * Unicode range 0x0591-0x05C7 covers Hebrew diacritical marks.
 */
fun String.stripNiqqud(): String =
    this.replace(Regex("[\u0591-\u05C7]"), "")

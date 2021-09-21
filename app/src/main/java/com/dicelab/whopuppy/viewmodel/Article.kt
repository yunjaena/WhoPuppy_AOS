package com.dicelab.whopuppy.viewmodel

sealed class Article

object Title : Article()
object Content : Article()
object Image : Article()
object Region : Article()

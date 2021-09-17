package com.yunjaena.whopuppy.viewmodel

sealed class Article

object Title : Article()
object Content : Article()
object Image : Article()
object Region : Article()

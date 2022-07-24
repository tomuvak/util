package com.tomuvak.util

expect class WeakReference<out T : Any>(target: T) { val targetOrNull: T? }

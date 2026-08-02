package com.garamohamed.plugins

import com.microsoft.playwright.*

object BrowserFactory {

    private lateinit var playwright: Playwright

    lateinit var browser: Browser
        private set
    lateinit var context: BrowserContext
        private set

    fun init() {
        playwright = Playwright.create()

        browser = playwright.chromium().launch(
            BrowserType.LaunchOptions()
                .setHeadless(false)
        )
        context = browser.newContext()

        println("Chromium started")
    }

    fun close(){
        context.close()
        browser.close()
        playwright.close()
    }
}
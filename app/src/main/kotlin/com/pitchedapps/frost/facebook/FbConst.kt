package com.pitchedapps.frost.facebook

/**
 * Created by Allan Wang on 2017-06-01.
 */

const val FACEBOOK_COM = "facebook.com"
const val FBCDN_NET = "fbcdn.net"
const val HTTPS_FACEBOOK_COM = "https://$FACEBOOK_COM"
const val FB_URL_BASE = "https://m.$FACEBOOK_COM/"
fun profilePictureUrl(id: Long) = "https://graph.facebook.com/$id/picture?type=large"
const val FB_LOGIN_URL = "${FB_URL_BASE}login"

const val USER_AGENT_FULL = "Mozilla/5.0 (Linux; Android 4.4.2; en-us; SAMSUNG SM-G900T Build/KOT49H) AppleWebKit/537.36 (KHTML, like Gecko) Version/1.6 Chrome/28.0.1500.94 Mobile Safari/537.36"
const val USER_AGENT_BASIC_OLD = "Mozilla/5.0 (Linux; Android 6.0) AppleWebKit/537.10+ (KHTML, like Gecko) Version/10.1.0.4633 Mobile Safari/537.10+"
const val USER_AGENT_MESSENGER = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36"
const val USER_AGENT_BASIC = USER_AGENT_MESSENGER

/**
 * Animation transition delay, just to ensure that the styles
 * have properly set in
 */
const val WEB_LOAD_DELAY = 50L
/**
 * Additional delay for transition when called from commit.
 * Note that transitions are also called from onFinish, so this value
 * will never make a load slower than it is
 */
const val WEB_COMMIT_LOAD_DELAY = 200L
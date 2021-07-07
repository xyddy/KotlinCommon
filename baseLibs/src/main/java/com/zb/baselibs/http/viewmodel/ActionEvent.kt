package com.zb.baselibs.http.viewmodel

import kotlinx.coroutines.Job

/**
 * @Author: leavesC
 * @Date: 2020/6/26 21:19
 * @Desc:
 * @GitHub：https://github.com/leavesC
 */
open class BaseActionEvent

class ShowLoadingEvent(val job: Job?,val msg: String?) : BaseActionEvent()

object DismissLoadingEvent : BaseActionEvent()

object FinishViewEvent : BaseActionEvent()

class ShowToastEvent(val message: String) : BaseActionEvent()
package org.example.backend.controller.payment

import com.alipay.api.AlipayRequest
import jakarta.servlet.http.HttpServletRequest
import org.example.backend.service.payment.PaymentService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/payment/callback")
class PaymentCallback (
    private val paymentService: PaymentService,
){
    @PostMapping("/alipay")
    fun handleAlipayCallback(request: HttpServletRequest,@RequestBody(required = false) body:Map<String,String>?):String{
        val params=request.parameterMap.mapValues {it.value[0]}
        val isValid=paymentService.verifyAlipayCallback(params)
        if(!isValid){
            return "failure"
        }
        val tradeNo=params["out_trade_no"]
        val tradeStatus=params["trade_status"]
        if(tradeNo!=null&&tradeStatus!=null){
            paymentService.processPaymentResult(tradeNo,tradeStatus)
        }
        return "success"
    }
}
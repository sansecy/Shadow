package com.tencent.mts.core.transform

import com.tencent.shadow.core.transform_kit.SpecificTransform
import com.tencent.shadow.core.transform_kit.TransformStep
import javassist.*


class MTSTransform : SpecificTransform() {
    override fun setup(allInputClass: Set<CtClass>) {
        mClassPool.importPackage("android.os")
        mClassPool.importPackage("android.view")
        val Bundle = mClassPool.get("android.os.Bundle")
        val Log = mClassPool.get("android.util.Log")
        val ViewGroup = mClassPool.get("android.view.ViewGroup")
        val View = mClassPool.get("android.view.View")
        val LayoutInflater = mClassPool.get("android.view.LayoutInflater")
        newStep(object : TransformStep {

            override fun filter(allInputClass: Set<CtClass>): Set<CtClass> {
                return allInputClass
            }

            override fun transform(ctClass: CtClass) {
                modifyMethod(ctClass, "onCreate")
//                methodStatistics(ctClass, "onCreate", arrayOf(Bundle))
                modifyMethod(ctClass, "onStart")
                modifyMethod(ctClass, "onResume")
                modifyMethod(ctClass, "onPause")
                modifyMethod(ctClass, "onRestart")
                modifyMethod(ctClass, "onDestroy")
                //fragment独有方法
                modifyMethod(ctClass, "onCreateView")
                modifyMethod(ctClass, "onViewCreated")
                modifyMethod(ctClass, "onDestroyView")
                modifyMethod(ctClass, "onBindViewHolder")
                modifyMethod(ctClass, "handleMessage")
                modifyMethod(ctClass, "onWindowFocusChanged")
                println("insert code success for ${ctClass.name} ")
            }
        })
    }

    private fun modifyMethod(ctClass: CtClass, methodName: String) {
        try {
            val method = ctClass.getDeclaredMethods(methodName)
            method.forEach {
                modifyMethodReal(ctClass, it)
            }
        } catch (e: Exception) {
        }
    }

    private fun modifyMethod(ctClass: CtClass, methodName: String, params: Array<CtClass>) {
        try {
            modifyMethodReal(ctClass, ctClass.getDeclaredMethod(methodName, params))
        } catch (e: Exception) {
        }
    }

    private fun modifyMethodReal(ctClass: CtClass, method: CtMethod) {
        try {
            method.addLocalVariable("mts_start_time", CtClass.longType)
            method.insertBefore("{  mts_start_time = System.currentTimeMillis(); }");
            method.addLocalVariable("mts_cost_time", CtClass.longType)
            method.insertAfter("{ mts_cost_time = System.currentTimeMillis() - mts_start_time; }");
            method.insertAfter("{ if(mts_cost_time>0){ android.util.Log.d(\"MTSC\", \"${ctClass.name}#${method.name}\" + \" : \" + mts_cost_time + \" ms \"); }}");
            method.insertAfter("{  com.sansecy.tools.MethodTracker.collect(\"${ctClass.name}#${method.name}\",mts_cost_time); }");
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}
/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.core.transform.specific

import com.tencent.shadow.core.transform_kit.ReplaceClassName
import com.tencent.shadow.core.transform_kit.SpecificTransform
import com.tencent.shadow.core.transform_kit.TransformStep
import javassist.CtClass

open class SimpleRenameTransform(private val fromToMap: Map<String, String>) : SpecificTransform() {
    override fun setup(allInputClass: Set<CtClass>) {
        newStep(object : TransformStep {
            override fun filter(allInputClass: Set<CtClass>): Set<CtClass> {
                val classSet = filterRefClasses(allInputClass, fromToMap.keys.toList()).filter {
                    !it.name.startsWith("com.tencent.shadow.sample.plugin.utils")
                }.toSet()
                classSet.forEach{
                    println("$name ++ ${it.name}")
                }
                return classSet
            }

            override fun transform(ctClass: CtClass) {
                fromToMap.forEach {
                    ReplaceClassName.replaceClassName(ctClass, it.key, it.value)
                }
            }
        })
    }
}
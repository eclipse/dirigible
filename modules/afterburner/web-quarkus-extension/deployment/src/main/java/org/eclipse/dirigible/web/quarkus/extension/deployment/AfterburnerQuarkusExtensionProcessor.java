/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.web.quarkus.extension.deployment;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.BytecodeTransformerBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.gizmo.Gizmo;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

class AfterburnerQuarkusExtensionProcessor {

    private static final String FEATURE = "afterburner-server-quarkus-extension";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void removeCompatibilityBridgeMethodsFromGitHubApi(BuildProducer<BytecodeTransformerBuildItem> bytecodeTransformers) {
        var uniClassName = "io.smallrye.mutiny.Uni";

        bytecodeTransformers.produce(new BytecodeTransformerBuildItem.Builder()
                .setClassToTransform(uniClassName)
                .setVisitorFunction((ignored, visitor) -> new RemoveBridgeMethodsClassVisitor(visitor, uniClassName))
                .build());
    }

    private static class RemoveBridgeMethodsClassVisitor extends ClassVisitor {

        public RemoveBridgeMethodsClassVisitor(ClassVisitor visitor, String className) {
            super(Gizmo.ASM_API_VERSION, visitor);
        }

        @Override
        public void visitEnd() {
            generateThen();
            super.visitEnd();
        }

        private void generateThen() {
            System.out.println("!!!! VM: generateThen start");
            MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "then", "(Lorg/graalvm/polyglot/Value;Lorg/graalvm/polyglot/Value;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKESTATIC, "org/eclipse/dirigible/web/quarkus/extension/UniThenUtil", "then", "(Lio/smallrye/mutiny/Uni;Lorg/graalvm/polyglot/Value;Lorg/graalvm/polyglot/Value;)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 3);
            System.out.println("!!!! VM: generateThen end");
        }
    }
}

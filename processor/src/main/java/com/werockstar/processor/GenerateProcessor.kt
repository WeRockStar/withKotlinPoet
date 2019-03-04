package com.werockstar.processor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.werockstar.annotation.Generate
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(GenerateProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class GenerateProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment?): Boolean {
        println("Processing")
        roundEnvironment?.getElementsAnnotatedWith(Generate::class.java)?.forEach { methodElement ->
            if (methodElement.kind != ElementKind.METHOD) {
                return false
            }

//            (methodElement as ExecutableElement).parameters.forEach { variableElement ->
//                generateNewMethod(
//                    methodElement,
//                    variableElement,
//                    processingEnv.elementUtils.getPackageOf(methodElement).toString()
//                )
//            }

            val packageOfMethod = processingEnv.elementUtils.getPackageOf(methodElement).toString()

            val generatedSourcesRoot: String = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()


            val file = File(generatedSourcesRoot)
            file.mkdir()

            val flux = FunSpec.constructorBuilder()
                .addParameter("isProductionMode", Boolean::class)
                .build()

            FileSpec.builder(packageOfMethod, "GeneratePath")
                .addType(
                    TypeSpec.classBuilder("GeneratePath")
                        .primaryConstructor(flux)
                        .addProperty(
                            PropertySpec.builder("isProductionMode", Boolean::class)
                                .initializer("isProductionMode")
                                .addModifiers(KModifier.PRIVATE)
                                .build()
                        )
                        .addFunction(
                            FunSpec.builder("getPath")
                                .returns(String::class)
                                .addModifiers(KModifier.PUBLIC)
                                .addParameter(
                                    methodElement.getAnnotation(Generate::class.java).path,
                                    String::class
                                )
                                .addStatement("return if(isProductionMode) \"Hi\" else \"World\"")
                                .build()
                        )
                        .build()
                )
                .build().writeTo(file)
        }

        return false
    }

    private fun generateNewMethod(method: ExecutableElement, variable: VariableElement, packageOfMethod: String) {
        val generatedSourcesRoot: String = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()


        val funcBuilder = FunSpec.builder("getPath")
            .addModifiers(KModifier.PUBLIC)
            .addParameter(
                method.getAnnotation(Generate::class.java).path,
                ClassName("com.werockstar.processor", "Path")
            )


        val file = File(generatedSourcesRoot)
        file.mkdir()
        FileSpec.builder(packageOfMethod, "GeneratePath").addFunction(funcBuilder.build()).build().writeTo(file)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }


    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Generate::class.java.canonicalName)
    }
}
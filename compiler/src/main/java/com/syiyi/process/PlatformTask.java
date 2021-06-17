package com.syiyi.process;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.syiyi.annotation.Platform;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

class PlatformTask extends BaseTask {

    PlatformTask(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public void process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!isLoaded) {

            TypeSpec.Builder classBuilder = TypeSpec.classBuilder("PlatformGen$");

            classBuilder
                    .addModifiers(Modifier.PUBLIC)
                    .addModifiers(Modifier.FINAL);

            TypeName string = ClassName.get(String.class);

            TypeName integer = ClassName.get(Integer.class);

            TypeName hashMapOfIntegerAndString = ParameterizedTypeName.get(ClassName.get(HashMap.class), integer, string);


            FieldSpec field = FieldSpec.builder(Map.class, "PLATFORMS")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("new $T()", hashMapOfIntegerAndString)
                    .build();

            classBuilder.addField(field);

            CodeBlock.Builder staticBlockBuilder = CodeBlock.builder();

            for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Platform.class)) {

                String platformClassName = getPackageName(annotatedElement) + "." + getSimpleName(annotatedElement);

                int platformId = annotatedElement.getAnnotation(Platform.class).value();

                staticBlockBuilder.addStatement(" PLATFORMS.put($L,$S);", platformId, platformClassName);
            }

            classBuilder.addInitializerBlock(staticBlockBuilder.build());

            try {
                TypeSpec classHelper = classBuilder.build();
                JavaFile javaFile = JavaFile.builder("com.syiyi.gen", classHelper).build();
                javaFile.writeTo(filer);
                isLoaded = true;
            } catch (Exception ex) {
                log("Exception:", ex.getMessage());
                isLoaded = false;
            }
        }
    }

    @Override
    public String getSupportedAnnotationType() {
        return Platform.class.getCanonicalName();
    }
}

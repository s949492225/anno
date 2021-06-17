package com.syiyi.process;

import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

interface ITask {

    void process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv);

    String getSupportedAnnotationType();

}

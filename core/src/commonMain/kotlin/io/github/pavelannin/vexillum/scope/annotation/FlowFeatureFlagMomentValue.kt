package io.github.pavelannin.vexillum.scope.annotation

import kotlin.annotation.AnnotationTarget.ANNOTATION_CLASS
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.CONSTRUCTOR
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.LOCAL_VARIABLE
import kotlin.annotation.AnnotationTarget.PROPERTY
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER
import kotlin.annotation.AnnotationTarget.PROPERTY_SETTER
import kotlin.annotation.AnnotationTarget.TYPEALIAS
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

/**
 * ###### EN:
 * The annotation indicates that the annotated code uses the feature flag's value
 * at a specific moment in time, which may become stale if the flag value changes later.
 *
 * ###### RU:
 * Аннотация указывает, что аннотированный код использует текущее значение
 * фича-флага в конкретный момент времени, которое может устареть
 * если значение флага изменится позже.
 */
@RequiresOptIn(
    level = RequiresOptIn.Level.WARNING,
    message = "The moment value of the feature flag being changed is used"
)
@Retention(AnnotationRetention.BINARY)
@Target(
    CLASS,
    ANNOTATION_CLASS,
    PROPERTY,
    FIELD,
    LOCAL_VARIABLE,
    VALUE_PARAMETER,
    CONSTRUCTOR,
    FUNCTION,
    PROPERTY_GETTER,
    PROPERTY_SETTER,
    TYPEALIAS
)
public annotation class FlowFeatureFlagMomentValue

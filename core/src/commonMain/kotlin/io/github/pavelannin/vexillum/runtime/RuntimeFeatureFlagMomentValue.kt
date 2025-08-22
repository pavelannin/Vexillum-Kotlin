package io.github.pavelannin.vexillum.runtime

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
 * This annotation indicates that the annotated code uses the current value
 * of a feature flag at a specific moment in time, which may become stale
 * if the flag value changes later.
 * ###### RU:
 * Эта аннотация указывает, что аннотированный код использует текущее значение
 * фичи флага в конкретный момент времени, которое может устареть
 * если значение флага изменится позже.
 */
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
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
public annotation class RuntimeFeatureFlagMomentValue

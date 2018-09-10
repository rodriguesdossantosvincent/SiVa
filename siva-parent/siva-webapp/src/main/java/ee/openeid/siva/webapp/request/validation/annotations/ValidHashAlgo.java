package ee.openeid.siva.webapp.request.validation.annotations;

import ee.openeid.siva.webapp.request.validation.AcceptedValue;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static ee.openeid.siva.webapp.request.validation.annotations.ValidHashAlgo.MESSAGE;

@AcceptValues(value = AcceptedValue.HASH_ALGO, message = MESSAGE)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface ValidHashAlgo {

    String MESSAGE = "{validation.error.message.hashAlgo}";

    String message() default MESSAGE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}

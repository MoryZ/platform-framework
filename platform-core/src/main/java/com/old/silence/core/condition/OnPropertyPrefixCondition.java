package com.old.silence.core.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotationPredicates;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Order(-2147483608)
class OnPropertyPrefixCondition extends SpringBootCondition {
    OnPropertyPrefixCondition() {
    }

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        List<AnnotationAttributes> allAnnotationAttributes = metadata.getAnnotations().stream(ConditionOnPropertyPrefix.class.getName())
                .filter(MergedAnnotationPredicates.unique(MergedAnnotation::getMetaTypes))
                .map((rec$) -> ((MergedAnnotation) rec$).asAnnotationAttributes(new MergedAnnotation.Adapt[0])).collect(Collectors.toList());
        ArrayList<ConditionMessage> noMatch = new ArrayList<>();
        ArrayList<ConditionMessage> match = new ArrayList<>();
        Iterator iterator = allAnnotationAttributes.iterator();

        while (iterator.hasNext()){
            AnnotationAttributes annotationAttributes = (AnnotationAttributes) iterator.next();
            ConditionOutcome outcome = this.determineOutCome(annotationAttributes, context.getEnvironment());
            (outcome.isMatch() ? match : noMatch).add(outcome.getConditionMessage());
        }

        return noMatch.isEmpty() ? ConditionOutcome.match(ConditionMessage.of(match))
                : ConditionOutcome.noMatch(ConditionMessage.of(noMatch));
    }

    private ConditionOutcome determineOutCome(AnnotationAttributes annotationAttributes, Environment environment) {
        String prefix = annotationAttributes.getString("value");
        MutablePropertySources sources = ((ConfigurableEnvironment) environment).getPropertySources();
        boolean found = StreamSupport.stream((Spliterators.spliteratorUnknownSize(sources.iterator(), 0)),false)
                .flatMap((source) -> Arrays.stream(getPropertyName(source))).anyMatch((name) -> name.startsWith(prefix));
        return found ? ConditionOutcome.match(ConditionMessage.forCondition(ConditionOnPropertyPrefix.class,
                new Object[]{prefix}).because("matched")) : ConditionOutcome.noMatch(ConditionMessage.forCondition(ConditionOnPropertyPrefix.class,
                        new Object[]{prefix}).found("different value in property prefix", "different value in properties prefix")
                .items(ConditionMessage.Style.QUOTE, new Object[]{prefix}));

    }

    public static String[] getPropertyName(PropertySource<?> propertySource){
        String[] propertyNames = null;
        if (propertySource instanceof EnumerablePropertySource){
            propertyNames = ((EnumerablePropertySource) propertySource).getPropertyNames();
        }
        return Objects.requireNonNullElse(propertyNames, ArrayUtils.EMPTY_STRING_ARRAY);
    }
}

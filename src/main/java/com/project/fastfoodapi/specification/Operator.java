package com.project.fastfoodapi.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

public enum Operator {

    EQUAL {
        public <T> Predicate build(Root<T> root, CriteriaBuilder cb, FilterRequest request, Predicate predicate) {
            Object value = request.getFieldType().parse(request.getValue().toString());
            if(request.isOr()){
                return cb.or(cb.equal(EntitySpecification.getExpression(root, request.getKey()), value), predicate);
            }
            return cb.and(cb.equal(EntitySpecification.getExpression(root, request.getKey()), value), predicate);
        }
    },

    NOT_EQUAL {
        public <T> Predicate build(Root<T> root, CriteriaBuilder cb, FilterRequest request, Predicate predicate) {
            Object value = request.getFieldType().parse(request.getValue().toString());
            if(request.isOr()){
                return cb.or(cb.notEqual(EntitySpecification.getExpression(root, request.getKey()), value), predicate);
            }
            return cb.and(cb.notEqual(EntitySpecification.getExpression(root, request.getKey()), value), predicate);
        }
    },

    LIKE {
        public <T> Predicate build(Root<T> root, CriteriaBuilder cb, FilterRequest request, Predicate predicate) {
            Expression<String> key = EntitySpecification.getExpression(root, request.getKey());
            if(request.isOr()){
                return cb.or(cb.like(cb.upper(key), "%" + request.getValue().toString().toUpperCase() + "%"), predicate);
            }
            return cb.and(cb.like(cb.upper(key), "%" + request.getValue().toString().toUpperCase() + "%"), predicate);
        }
    },

    IN {
        public <T> Predicate build(Root<T> root, CriteriaBuilder cb, FilterRequest request, Predicate predicate) {
            List<Object> values = request.getValues();
            CriteriaBuilder.In<Object> inClause = cb.in(EntitySpecification.getExpression(root, request.getKey()));
            for (Object value : values) {
                inClause.value(request.getFieldType().parse(value.toString()));
            }
            if(request.isOr()){
                return cb.or(inClause, predicate);
            }
            return cb.and(inClause, predicate);
        }
    },

    BETWEEN {
        public <T> Predicate build(Root<T> root, CriteriaBuilder cb, FilterRequest request, Predicate predicate) {
            Object value = request.getFieldType().parse(request.getValue().toString());
            Object valueTo = request.getFieldType().parse(request.getValueTo().toString());
            if (request.getFieldType() == FieldType.DATE) {
                LocalDateTime startDate = (LocalDateTime) value;
                LocalDateTime endDate = (LocalDateTime) valueTo;
                Expression<LocalDateTime> key = EntitySpecification.getExpression(root, request.getKey());
                if(request.isOr()){
                    return cb.or(cb.and(cb.greaterThanOrEqualTo(key, startDate), cb.lessThanOrEqualTo(key, endDate)), predicate);
                }
                return cb.and(cb.and(cb.greaterThanOrEqualTo(key, startDate), cb.lessThanOrEqualTo(key, endDate)), predicate);
            }

            if (request.getFieldType() != FieldType.CHAR && request.getFieldType() != FieldType.BOOLEAN) {
                Number start = (Number) value;
                Number end = (Number) valueTo;
                Expression<Number> key = EntitySpecification.getExpression(root, request.getKey());
                if(request.isOr()){
                    return cb.or(cb.and(cb.ge(key, start), cb.le(key, end)), predicate);
                }
                return cb.and(cb.and(cb.ge(key, start), cb.le(key, end)), predicate);
            }

            return predicate;
        }
    };

    public abstract <T> Predicate build(Root<T> root, CriteriaBuilder cb, FilterRequest request, Predicate predicate);

}

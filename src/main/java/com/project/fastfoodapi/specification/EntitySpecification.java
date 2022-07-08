package com.project.fastfoodapi.specification;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class EntitySpecification<T> implements Specification<T> {


    private final transient SearchRequest request;

    public static Pageable getPageable(Integer page, Integer size) {
        return PageRequest.of(Objects.requireNonNullElse(page, 0), Objects.requireNonNullElse(size, 100));
    }

    public static <T>Expression<T> getExpression(Root<?> root, String requestKey){
        Expression<T> key;
        String[] split = requestKey.split("\\.");
        assert split[0]!=null;
        Path<?> path = root.get(split[0]);
        for (int i = 1; i < split.length; i++) {
            if(i==split.length-1){
                break;
            }
            path=path.get(split[i]);
        }
        if(split.length>1){
            key=path.get(split[split.length-1]);
        }else {
            key=root.get(requestKey);
        }
        return key;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate predicate = cb.equal(cb.literal(Boolean.TRUE), Boolean.TRUE);

        for (FilterRequest filter : this.request.getFilters()) {
            if(filter==null)continue;
            predicate = filter.getOperator().build(root, cb, filter, predicate);
        }

        List<Order> orders = new ArrayList<>();
        for (SortRequest sort : this.request.getSorts()) {
            if(sort==null)continue;
            orders.add(sort.getDirection().build(root, cb, sort));
        }

        query.orderBy(orders);
        return predicate;
    }
}

package com.example.mysterycard.specification;

import com.example.mysterycard.entity.ListSeller;
import com.example.mysterycard.enums.Rarity;
import com.example.mysterycard.enums.Status;
import org.springframework.data.jpa.domain.Specification;

public class ListSellerSpecification {
    public static Specification<ListSeller> findByCardName(String keySearch)
    {

        return (root, query, cb) -> {
            if(keySearch == null || keySearch.isEmpty()) {
                return cb.conjunction();
            }
          return
                    cb.like(cb.lower(root.get("card").get("name")),"%"+keySearch.toLowerCase()+"%");

        };
    }
    public static Specification<ListSeller> findByRarityCard(Rarity rarity)
    {

        return (root, query, cb) -> {
            if(rarity == null ) {
                return cb.conjunction();
            }
            return  cb.equal(root.get("card").get("rarity"),rarity);
        };
    }
    public static Specification<ListSeller> findByStatus(Status status)
    {
        return (root, query, cb) -> {
            if(status == null ) {
                return cb.conjunction();
            }
            return  cb.equal(root.get("status"),status);
        };
    }
    public static Specification<ListSeller> findByQuanity()
    {
        return (root, query, cb) -> {
            return  cb.ge(root.get("quantity"),1);
        };
    }
    public static Specification<ListSeller> findByCardPrice(Double min, Double max)
    {

        return (root, query, cb) -> {
           if(min == 0 && max == 0)
           {
               return cb.conjunction();
           }

            return
                    cb.between(root.get("card").get("basePrice"), min, max);
        };
    }
}

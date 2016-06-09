package ru.megy.repository;

import org.springframework.data.repository.CrudRepository;
import ru.megy.repository.entity.Item;

public interface ItemRepository extends CrudRepository<Item, Long> {
}

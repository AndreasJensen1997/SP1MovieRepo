package app.daos;

import java.util.List;

public interface IDAO <T, I>{

    T create(T t);

    T get(I i);

    List<T> getAll();

    T update(T t);

    boolean delete(T t);

}

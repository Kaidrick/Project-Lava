package moe.ofs.backend.common;

import moe.ofs.backend.domain.dcs.BaseEntity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface UpdatableService<T extends BaseEntity> {

    T update(T updateObject);

    void add(T newObject);

    void remove(T obsoleteObject);

    @Deprecated
    default void cycle(List<T> list) {}

    @Deprecated
    default boolean updatable(T update, T record) {
        return false;
    }

    /**
     * Update fields on the record object with values of the fields from the update object.
     * In JDK 8, PropertyDescriptor is used to retrieve the field value from update object and then set to record object.
     *
     * When compiling with JDK 11 or later, the constructor of PropertyDescriptor should be invoked
     * with the first parameter being a String value casting to String; the casting is somehow required to
     * avoid PropertyInfo access issue.
     *
     * In JDK 11, the Field#set method can be used instead.
     *
     * @param record the record object whose fields should be updated
     * @param update the update object whose fields will be used to insert to record object
     * @return List for String that contains the fields updated
     */
    default List<String> updateFields(T record, T update) {
        List<Field> fields = Arrays.asList(update.getClass().getDeclaredFields());
        List<String> updatedFields = new ArrayList<>();
        fields.forEach(field -> {
            field.setAccessible(true);
            try {
                // if the field is not null,
                // FIXME: export object field equality needs to be defined; need to rework parking info binary data
                if (field.get(update) != null && !field.get(update).equals(field.get(record))) {

                    field.set(record, field.get(update));  // adapt changes to set method in newer JDK

                    updatedFields.add(field.getName());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        return updatedFields;
    }
}

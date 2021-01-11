package moe.ofs.backend.common;

import moe.ofs.backend.domain.dcs.BaseEntity;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
     * Update fields on the record object with values of the fields from the update object
     * @param record the record object whose fields should be
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
                    PropertyDescriptor propertyDescriptor =
                            new PropertyDescriptor(field.getName(), record.getClass());

                    // invoke setter method on this field
                    propertyDescriptor.getWriteMethod().invoke(record, field.get(update));

                    updatedFields.add(field.getName());
                }
            } catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });

        return updatedFields;
    }
}

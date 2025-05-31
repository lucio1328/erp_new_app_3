package com.lucio.erp_new_app_3.utils;

import java.util.Collections;
import java.util.List;

public class PaginationUtils {

    /**
     * Retourne une sous-liste paginée d'une liste d'éléments.
     *
     * @param fullList La liste complète à paginer.
     * @param page     Numéro de la page (0-indexé).
     * @param size     Taille de la page (nombre d'éléments par page).
     * @param <T>      Type des éléments de la liste.
     * @return Une sous-liste correspondant à la page demandée.
     */
    public static <T> List<T> paginate(List<T> fullList, int page, int size) {
        if (fullList == null || fullList.isEmpty()) {
            return Collections.emptyList();
        }

        int total = fullList.size();
        int fromIndex = Math.min(page * size, total);
        int toIndex = Math.min(fromIndex + size, total);

        return fullList.subList(fromIndex, toIndex);
    }

    /**
     * Calcule le nombre total de pages à partir d'une liste et d'une taille.
     *
     * @param totalItems Nombre total d'éléments.
     * @param size       Taille d'une page.
     * @return Nombre total de pages.
     */
    public static int getTotalPages(int totalItems, int size) {
        return (int) Math.ceil((double) totalItems / size);
    }
}

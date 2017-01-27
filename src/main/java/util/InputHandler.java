package util;

import sql.DatabaseServer;
public class InputHandler {

    public DataPackage handle(DataPackage dataPackage) {


        switch (dataPackage.getDetails()) {
            case "login" :
                //TODO: to co na fejsie pisałem
                return dataPackage; // Return to zawsze wynik dla klienta

            case "disconnect" :
                //TODO: usunąć Connection z listy z klasy Server, zatrzymać wątek w Connection
                dataPackage.setDetails("disconnected");
                return  dataPackage;

            case "insert" :
                //Przykład: powinien zwrócić zinsertowany obiekt w datapackage, to wypełni jego pole id i zwróci do klienta
                Object object = dataPackage.getObject();
                object = insert(object);
                dataPackage.setObject(object);
                return dataPackage;

            default:
                return null;
        }

    }

    /**
     * to stworzy dowolny projekt, błąd lub użytkownika, wystarczy żeby rozróżnić że to insert
     * @param object obiekt klasy {@link Issue} {@link Project} {@link User}
     * @return zwraca obiekt z id z bazy, nie dodaje issue i user do projektu jeśli są na liście,
     * to robi dopiero update dla projektu
     */
    private Object insert(Object object) {
        DatabaseServer db = DatabaseServer.getInstance();
        object = db.insert(object);

        return object;
    }

    /**
     * Aktualizuje, jeśli aktualizujemy projekt to stworzą się automatycznie nieistniejący użytkownicy i błędy o
     * oraz się zaktualizują
     * @param object obiekt klasy {@link Issue} {@link Project} {@link User}
     * @return Object ilość zmian w projekcie
     */
    private Object update(Object object) {
        //update
        DatabaseServer db = DatabaseServer.getInstance();
        return db.update(object);
    }

    /**
     * Usuwanie z bazy po id w obiekcie
     * Jeśli usuwamy projekt usunie też jego błędy, użytkownicy zostają
     * @param object obiekt klasy {@link Issue} {@link Project} {@link User}
     * @return ilość zmian w bazie, jak zero to błąd
     */
    private Object delete(Object object) {
        DatabaseServer db = DatabaseServer.getInstance();
        return db.delete(object);
    }

    /**
     * Pobiera dane z bazy do obiektu
     * Jeśli to projekt, pobierze także wszystkie błędy i użytkowników do list
     * @param object Musi zawierać id obiektu i być odpowiednią klasą
     * @return Obiekt reprezentujący dane z bazy
     */
    private Object select(Object object) {
        DatabaseServer db = DatabaseServer.getInstance();
        return db.select(object);
    }
    //TODO: zapis projektu (plików) na serwerze, mogą być np zapisywane w folderach o nazwie..
    // jak tytuł projektu zamiast ścieżek jakichś

    //TODO: pobieranie plików z serwera
    //TODO: String w żadnym obiekcie nie może zawierac apostrofu '

    private Object login (Object object) {
        //TODO: logowanie, ustawia odpowiednie connection.logged na true oraz ustawia connection.user na zalogowanego
        //sprawdzić czy już jest zalogowany (sprawdzenie czy taki jest już przypisany do jakiegoś Connection z listy
        return object;
    }

    private Object logout (Object object) {
        //TODO: Wylogowywanie, ustawia odpowiednie connection.logged na false oraz usuwa connection.user na null
        return object;
    }

    private Object register(Object object) {
        //TODO: rejestracja, musi robić insert użytkownika, i musimy gdzieś przechowywać hasło i login tego jeszcze nie ma
        //sprawdzić czy istnieje
        return object;
    }
}

import javax.persistence.*;
import java.util.ArrayList;

public class KontoDAO {
    private EntityManagerFactory entityManagerFactory;

    public KontoDAO(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    private EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    private void closeEntityManager(EntityManager manager) {
        if (manager != null && manager.isOpen()) {
            manager.close();
        }
    }

    public void lagreNyKonto(Konto konto) {
        EntityManager manager = getEntityManager();
        try {
            manager.getTransaction().begin();
            manager.persist(konto);
            manager.getTransaction().commit();
        } finally {
            closeEntityManager(manager);
        }
    }

    public void oppdaterKonto(Konto konto) {
        EntityManager manager = getEntityManager();
        try {
            manager.getTransaction().begin();
            manager.merge(konto);
            manager.getTransaction().commit();
        } finally {
            closeEntityManager(manager);
        }
    }

    public Konto getKonto(int kontonr) {
        EntityManager manager = getEntityManager();
        try {
            return manager.find(Konto.class, kontonr);
        } finally {
            closeEntityManager(manager);
        }
    }

    public ArrayList<Konto> getKontoerOverSaldo(double saldo) {
        EntityManager manager = getEntityManager();
        try {
            Query query = manager.createQuery("SELECT OBJECT(k) FROM Konto k WHERE k.saldo >= :belop");
            query.setParameter("belop", saldo);
            return new ArrayList<>(query.getResultList());
        } finally {
            closeEntityManager(manager);
        }
    }

    public void overfor(int kontonrFra, int kontonrTil, double belop) {
        EntityManager manager = getEntityManager();
        try {
            Konto kontoFra = manager.find(Konto.class, kontonrFra);
            Konto kontoTil = manager.find(Konto.class, kontonrTil);
            kontoFra.trekk(belop);
            if (kontoFra.getSaldo() < 0) {
                return;
            }
            kontoTil.trekk(-belop);

            manager.getTransaction().begin();
            manager.merge(kontoFra);
            manager.merge(kontoTil);
            manager.getTransaction().commit();
        } finally {
            closeEntityManager(manager);
        }
    }

    public void deleteKonto(int kontonr) {
        EntityManager manager = getEntityManager();
        try {
            manager.getTransaction().begin();
            Konto temp = manager.find(Konto.class, kontonr);
            manager.remove(temp);
            manager.getTransaction().commit();
        } finally {
            closeEntityManager(manager);
        }
    }

    public void lagreNyKontoLocked(KontoLocked konto) {
        EntityManager manager = getEntityManager();
        try {
            manager.getTransaction().begin();
            manager.persist(konto);
            manager.getTransaction().commit();
        } finally {
            closeEntityManager(manager);
        }
    }

    public KontoLocked getKontoLocked(int kontonr) {
        EntityManager manager = getEntityManager();
        try {
            return manager.find(KontoLocked.class, kontonr);
        } finally {
            closeEntityManager(manager);
        }
    }

    public void overforLocked(int kontonrFra, int kontonrTil, double belop) {
        boolean overfort = false;
        while (!overfort) {
            EntityManager manager = getEntityManager();
            try {
                KontoLocked kontoFra = manager.find(KontoLocked.class, kontonrFra);
                KontoLocked kontoTil = manager.find(KontoLocked.class, kontonrTil);
                kontoFra.trekk(belop);
                if (kontoFra.getSaldo() < 0) {
                    return;
                }
                kontoTil.trekk(-belop);

                manager.getTransaction().begin();
                manager.merge(kontoFra);
                manager.merge(kontoTil);
                manager.getTransaction().commit();
                overfort = true;
            } catch (OptimisticLockException | RollbackException e) {
                System.out.println("Fanget OptimisticLockException, prøver igjen");
            } finally {
                closeEntityManager(manager);
            }
        }
    }

    public void oppdaterKontoLocked(KontoLocked konto) {
        EntityManager manager = getEntityManager();
        try {
            manager.getTransaction().begin();
            manager.merge(konto);
            manager.getTransaction().commit();
        } finally {
            closeEntityManager(manager);
        }
    }

    public void deleteKontoLocked(int kontonr) {
        EntityManager manager = getEntityManager();
        try {
            manager.getTransaction().begin();
            KontoLocked temp = manager.find(KontoLocked.class, kontonr);
            manager.remove(temp);
            manager.getTransaction().commit();
        } finally {
            closeEntityManager(manager);
        }
    }
}
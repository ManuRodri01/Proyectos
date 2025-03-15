/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistence;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import logic.ListTask;
import persistence.exceptions.NonexistentEntityException;

/**
 *
 * @author galax
 */
public class ListTaskJpaController implements Serializable {

    public ListTaskJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    public ListTaskJpaController(){
        emf = Persistence.createEntityManagerFactory("toDoListPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ListTask listTask) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(listTask);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ListTask listTask) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            listTask = em.merge(listTask);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = listTask.getId();
                if (findListTask(id) == null) {
                    throw new NonexistentEntityException("The listTask with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ListTask listTask;
            try {
                listTask = em.getReference(ListTask.class, id);
                listTask.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The listTask with id " + id + " no longer exists.", enfe);
            }
            em.remove(listTask);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ListTask> findListTaskEntities() {
        return findListTaskEntities(true, -1, -1);
    }

    public List<ListTask> findListTaskEntities(int maxResults, int firstResult) {
        return findListTaskEntities(false, maxResults, firstResult);
    }

    private List<ListTask> findListTaskEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ListTask.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public ListTask findListTask(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ListTask.class, id);
        } finally {
            em.close();
        }
    }

    public int getListTaskCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ListTask> rt = cq.from(ListTask.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

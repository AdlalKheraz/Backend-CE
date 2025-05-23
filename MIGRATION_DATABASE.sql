-- Migration des bases de données pour les améliorations de gestion des retours vides
-- À exécuter sur chaque base de données des microservices

-- =====================================================
-- MEDIA SERVICE DATABASE MIGRATION
-- =====================================================

-- Ajouter les colonnes title et description à la table media
ALTER TABLE media 
ADD COLUMN title VARCHAR(255),
ADD COLUMN description VARCHAR(1000);

-- Mettre à jour les enregistrements existants avec des valeurs par défaut
UPDATE media 
SET title = CONCAT('Media_', id),
    description = CONCAT('Description pour le média ', id)
WHERE title IS NULL OR description IS NULL;

-- =====================================================
-- EVENT SERVICE DATABASE MIGRATION
-- =====================================================

-- Aucune modification de structure nécessaire pour le service événements
-- Les nouvelles fonctionnalités utilisent les tables existantes

-- =====================================================
-- AUTH SERVICE DATABASE MIGRATION
-- =====================================================

-- Aucune modification de structure nécessaire pour le service authentification
-- Les nouvelles fonctionnalités utilisent les tables existantes

-- =====================================================
-- VÉRIFICATIONS POST-MIGRATION
-- =====================================================

-- Vérifier la structure de la table media
DESCRIBE media;

-- Vérifier que les données ont été migrées correctement
SELECT id, url, title, description, type, event_id 
FROM media 
LIMIT 5;

-- Compter le nombre d'enregistrements dans chaque table
SELECT 'media' as table_name, COUNT(*) as count FROM media
UNION ALL
SELECT 'events' as table_name, COUNT(*) as count FROM events
UNION ALL
SELECT 'civilizations' as table_name, COUNT(*) as count FROM civilizations
UNION ALL
SELECT 'comments' as table_name, COUNT(*) as count FROM comments
UNION ALL
SELECT 'users' as table_name, COUNT(*) as count FROM users; 
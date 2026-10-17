DROP INDEX IF EXISTS idx_item_exercicio_item_agrupador_id;

ALTER TABLE item_exercicio DROP COLUMN IF EXISTS item_agrupador_id;

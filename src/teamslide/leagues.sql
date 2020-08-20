-- :name create-leagues-table
-- :command :execute
-- :result :raw
create table if not exists leagues (
  id serial primary key,
  name varchar(40),
  price int,
  location geography
)

-- :name insert-league :! :n
insert into leagues (name, price, location)
values (:name, :price, :location)

-- :name all-leagues-within-radius-budget :? :*
select * from leagues
where ST_DWithin(location, :location, :radius, true)
and price <= :budget
order by price


create view v_adjacent_detectors as
select 1 dir, a.equip_id,   
(select top 1 equip_id from v_equip_config b1 where b1.equip_type='dtt' and b1.detector_type = 'M' and b1.traffic_data_enabled = 'Y' and b1.expway_code=a.expway_code and b1.dir=1 and a.dir=1 and b1.km_marking>a.km_marking order by km_marking asc) first_equip_id,
(select top 1 equip_id from v_equip_config b2 where b2.equip_type='dtt' and b2.detector_type = 'M' and b2.traffic_data_enabled = 'Y' and b2.expway_code=a.expway_code and b2.dir=1 and a.dir=1 and b2.km_marking<a.km_marking order by km_marking desc) second_equip_id
from v_equip_config a
where equip_type='dtt'
and dir=1
and detector_type = 'M' and traffic_data_enabled = 'Y'
union all
select 2 dir, a.equip_id,   
(select top 1 equip_id from v_equip_config b1 where b1.equip_type='dtt' and b1.detector_type = 'M' and b1.traffic_data_enabled = 'Y' and b1.expway_code=a.expway_code and b1.dir=2 and a.dir=2 and b1.km_marking<a.km_marking order by km_marking desc) first_equip_id,
(select top 1 equip_id from v_equip_config b2 where b2.equip_type='dtt' and b2.detector_type = 'M' and b2.traffic_data_enabled = 'Y' and b2.expway_code=a.expway_code and b2.dir=2 and a.dir=2 and b2.km_marking>a.km_marking order by km_marking asc) second_equip_id
from v_equip_config a
where equip_type='dtt'
and dir=2
and detector_type = 'M' and traffic_data_enabled = 'Y'
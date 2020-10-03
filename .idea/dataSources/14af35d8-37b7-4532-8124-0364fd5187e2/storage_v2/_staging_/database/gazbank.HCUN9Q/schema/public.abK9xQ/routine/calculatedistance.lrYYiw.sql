create function calculatedistance(lat1 double precision, lat2 double precision, lon1 double precision, lon2 double precision) returns double precision
    language plpgsql
as
$$
declare distance double precision;
    begin
        select pow(sin(radians((lon2 - lon1)/2)), 2) * cos(radians(lat2))* cos(radians(lat1)) + pow(sin(radians((lat2 - lat1)/2)), 2) into distance;
        select 2 * atan2(sqrt(distance), sqrt(1 - distance)) * 6371 into distance;
    return distance;
    end;
$$;

alter function calculatedistance(double precision, double precision, double precision, double precision) owner to postgres;


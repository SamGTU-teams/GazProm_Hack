create function calculatedistance(lat1 double precision, lat2 double precision, lon1 double precision, lon2 double precision) returns double precision
    language plpgsql
as
$$
declare distance float;
    declare temp double precision;
    begin
        select pow(sin(radians((lon2 - lon1)/2)), 2) * cos(radians(lat2))* cos(radians(lat1)) + pow(sin(radians((lat2 - lat1)/2)), 2) into temp;
        select (2 * cast(atan2(sqrt(temp), sqrt(1 - temp)) as float) * 6371) into distance;
    return distance;
    end;
$$;

alter function calculatedistance(double precision, double precision, double precision, double precision) owner to postgres;


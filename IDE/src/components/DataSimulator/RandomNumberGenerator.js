let spareRandom = null;

export function normalRandom()
{
    let val, u, v, s, mul;

    if(spareRandom !== null)
    {
        val = spareRandom;
        spareRandom = null;
    }
    else
    {
        do
        {
            u = Math.random()*2-1;
            v = Math.random()*2-1;

            s = u*u+v*v;
        } while(s === 0 || s >= 1);

        mul = Math.sqrt(-2 * Math.log(s) / s);

        val = u * mul;
        spareRandom = v * mul;
    }

    return val;
}


// Standard Normal variate using Box-Muller transform.
function randn_bm() {
    var u = 0, v = 0;
    while(u === 0) u = Math.random(); //Converting [0,1) to (0,1)
    while(v === 0) v = Math.random();
    return Math.sqrt( -2.0 * Math.log( u ) ) * Math.cos( 2.0 * Math.PI * v );
}

export function getRandomInt(max) {
    return Math.floor(Math.random() * max);
}

export function randomDate(start, end) {
    return new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime()));
}

export function normalRandomInRange(min, max)
{
    let val;
    do
    {
        val = normalRandom();
    } while(val < min || val > max);

    return val;
}

export function normalRandomScaled(mean, stddev)
{
    let r = (randn_bm() - .5)*2;

    r = r * stddev + mean;

    return Math.round(r);
}

export function lnRandomScaled(gmean, gstddev)
{
    let r = normalRandom();

    r = r * Math.log(gstddev) + Math.log(gmean);

    return Math.round(Math.exp(r));
}
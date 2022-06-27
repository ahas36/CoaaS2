mins = ' minutes'
seconds_in_day = 24 * 60 * 60
variations = {
    'static-line':6,
    'gradient-line':5,
    'super-up':2,
    'up':1,
    'random':0,
    'down':-1, 
    'super-down':-2
}

# The above variation types are as follows:
# static-line = the value doesn't change over time (time-invariant)
# gradient-line = the value monotonously vary linearly (step-wise)
# random = the value increase, and then decrease as in a normal distribution (bell curve like variation)
# up = randomly increasing value
# down = radomly decreasing value
# super-up = distribution after super imposing 'up' with 'random'
# super-down = distribution after super imposing 'down' with 'random'

#1. There are a couple inefficiencies with this approach:
#- first, the use of variables sum, and i are unnecessary.
#- repeated calls to the user-level + operator are also not optimal
#- even the print statement is unnecessary 
# Using the built-in 'sum(range(0,1000000))' eliminates these.

print sum(range(0,1000000));
a =[5,4,3,3,4,2,1];
print a;
b =[4,3,2,3,2,1,5];
print b;
a =list(set(a));
print a;
b =list(set(b));
print b;
print [i+j for i,j in zip(a,b)]

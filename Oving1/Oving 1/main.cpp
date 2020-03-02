//
//  main.cpp
//  Oving 1
//
//  Created by Ian Evangelista on 09/02/2020.
//  Copyright © 2020 Ian Evangelista. All rights reserved.
//

#include <iostream>
#include <thread>
#include <list>
#include <math.h>
using namespace std;
list<int> primes;
list<int>::iterator it = primes.begin();

void findPrimes(int low, int high){
    bool isPrime;
    if(low < 2)low = 2;
    while (low < high){
           isPrime = true;
           for(int i = 2; i < sqrt(low); i++)
           {
               if(low % i == 0) {
                   isPrime = false;
                   break;
               }
           }
           if (isPrime) {
               primes.insert(it, low);
           }
           low++;
       }
}
void getPrimes(int threads, int low, int high){
    std::thread myThreads[threads];
    int partition = ((high-low) / threads);
    int splitCurr = partition;
    int currLow = low;
    
    for (int i=0; i < threads; i++){
        myThreads[i] = thread(findPrimes, currLow, splitCurr);
        currLow = splitCurr+1;
        splitCurr += partition;
    }
    
    for (int i=0; i<threads; i++){
        myThreads[i].join();
    }
}
int main() {
    getPrimes(7, 0, 100);
    primes.sort();
    for (list<int>::iterator i = primes.begin(); i != primes.end(); i++){
          cout << *i << " ";
    }
}

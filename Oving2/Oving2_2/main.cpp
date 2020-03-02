//
//  main.cpp
//  Oving2_2
//
//  Created by Ian Evangelista on 25/02/2020.
//  Copyright © 2020 Ian Evangelista. All rights reserved.
//

#include <thread>
#include <list>
#include <mutex>
#include <condition_variable>
#include <vector>
#include <iostream>
#include <mutex>

using namespace std;

class Workers {
    int amount;
    vector<thread> threads;
    mutex task_mutex;
    condition_variable cv;
    int counter;
    list<function<void()>> tasks;
    mutex done_mutex;
    bool done = false;


public:
    Workers(int amountInput) {
        counter = amountInput;
    }

    void start() {

        for (int i = 0; i < counter; i++) {
            function<void()> task;
            threads.emplace_back([this, &task] {
                while (true) {
                    function<void()> task;
                    {
                        unique_lock<mutex> ul(this->task_mutex);
                        this->cv.wait(ul, [this] {
                            return this->done || !this->tasks.empty();
                        });
                        if (this->tasks.empty() && this->done)
                            return;
                        task = *tasks.begin();
                        tasks.pop_front();
                    }
                    task();
                }
            });

        };



    }

    void stop() {
        {
            unique_lock<mutex> uniqueLock(this->done_mutex);
            done = true;
        }
        this->cv.notify_all();
        for (auto &t : this->threads) t.join();
        threads.empty();
    }


    void post(function<void()> f) {
        {
            unique_lock<mutex> uniqueLock(task_mutex);
            tasks.emplace_back(f);
        }
        cv.notify_one();
    };

    void post_timeout(function<void()> f, int ms) {
        this_thread::sleep_for(chrono::milliseconds(ms));
        {
            unique_lock<mutex> uniqueLock(task_mutex);
            tasks.emplace_back(f);
        }
        cv.notify_one();


    }

};

using namespace std;
int main() {
    mutex task_mutex;

    Workers worker_threads(4);
    Workers event_loop(1);
    worker_threads.start();
    event_loop.start();

    worker_threads.post([] {
        cout << "WORKERTHREADS TASK 1 running\n";
    });

    worker_threads.post([] {
        cout << "WORKERTHREADS TASK 2 running\n";
    });

    event_loop.post([] {
        cout << "EVENTLOOP Task 1 running\n";
    });

    event_loop.post([] {
        cout << "EVENTLOOP Task 2 running\n";
    });

    event_loop.post_timeout([] {
        cout << "EVENTLOOP Task 3 running\n";
    }, 2000);

    event_loop.post_timeout([] {
        cout << "EVENTLOOP Task 4 running\n";
    }, 100);

    worker_threads.stop();
    event_loop.stop();

    return 0;
}

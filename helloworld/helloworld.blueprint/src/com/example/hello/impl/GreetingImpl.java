package com.example.hello.impl;

import com.example.hello.Greeting;

public class GreetingImpl implements Greeting {

	@Override
	public void sayHello(String name) {
		System.out.println("Hello " + name);
	}

}


package com.jfixby.red.geometry;

import java.util.ArrayList;

import com.jfixby.cmns.api.floatn.Float2;
import com.jfixby.cmns.api.geometry.ComposedProjection;
import com.jfixby.cmns.api.geometry.Projection;
import com.jfixby.cmns.api.geometry.ProjectionsStack;

public final class RedProjectionsStack implements ProjectionsStack, ComposedProjection {

	final ArrayList<Projection> stack = new ArrayList<Projection>();

	public RedProjectionsStack () {
	}

	public RedProjectionsStack (final Projection[] sequence) {
		for (int i = 0; i < sequence.length; i++) {
			final Projection p = sequence[i];
			this.push(p);
		}
	}

	@Override
	public void push (final Projection projection) {
		this.stack.add(projection);
	}

	@Override
	public Projection pop () {
		return this.stack.remove(this.stack.size() - 1);
	}

	@Override
	public Projection peek () {
		return this.stack.get(this.stack.size() - 1);
	}

	@Override
	public int size () {
		return this.stack.size();
	}

	@Override
	public void project (final Float2 point) {
		if (this.stack.size() == 0) {
			return;
		}

		for (int i = this.size() - 1; i >= 0; i--) {
			final Projection projection_i = this.stack.get(i);
			projection_i.project(point);
		}
	}

}
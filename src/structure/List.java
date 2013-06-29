package structure;

public class List<T> {
	protected aux<T> l;

	public void add(T a) {
		l = l == null ? new aux<T>(a) : l.add(a);
	}
	
	public void addOnce(T a) {
		if(l==null){
			l= new aux<T>(a);
		}
		else{
			l=l.contain(a)==0 ? l.add(a) : l;
		}
	}

	public void remove(T a) {
		l = l == null ? null : l.delete(a);
	}

	public boolean isEmpty() {
		return l == null;
	}

	public int contains(T a) {
		if (l == null) {
			return 0;
		} else {
			return l.contain(a);
		}
	}

	public Browse<T> browse() {
		return new Browse<T>(this);
	}

	public T get(int i) {
		if (l == null || l.length() < i) {
			return null;
		} else {
			aux<T> temp = l;
			int count = 0;
			while (count < i) {
				temp = temp.next;
				count++;
			}
			return temp.node;
		}
	}

	public void reverse() {
		Browse<T> b = this.browse();
		List<T> res = new List<T>();
		while (b.hasNext()) {
			res.add(b.next());
		}
		this.l = res.l;
	}

	public int size() {
		if (l == null) {
			return 0;
		}
		return l.length();
	}

	public List<T> clone() {
		aux<T> temp = l == null ? null : l.clone();
		return new List<T>(temp);
	}

	public List() {
		l = null;
	}

	public List(T a) {
		l = new aux<T>(a);
	}

	private List(aux<T> l) {
		this.l = l;
	}

	public String toString() {
		if (l == null) {
			return "[]";
		} else {
			return l.toString();
		}
	}

	static protected class aux<T> {

		public T node;
		public aux<T> next;
		private int length;

		public String toString() {
			String s = "[";
			s += "\n" + node.toString();
			aux<T> l = next;
			while (l != null) {
				s += "\n" + l.node.toString();
				l = l.next;
			}
			s += "]\n";
			return s;
		}

		public boolean equals(aux<T> m) {
			boolean res = this.length() == m.length();
			if (res) {
				aux<T> t1 = this;
				aux<T> t2 = m;
				while (t1 != null) {
					res = res && t1.node.equals(t2.node);
					t1 = t1.next;
					t2 = t2.next;
				}

			}
			return res;
		}

		public aux<T> clone() {
			aux<T> temp = new aux<T>(node);
			aux<T> copy = next;
			while (copy != null) {
				temp = temp.add(copy.node);
				copy = copy.next;
			}
			return temp;
		}

		public aux() {
			node = null;
			next = null;
			length=0;
		}

		public aux(T n) {
			node = n;
			next = null;
			length=1;
		}

		public aux<T> add(T n) {
			aux<T> l = new aux<T>(n);
			l.next = this;
			l.length=l.next.length+1;
			return l;
		}

		public aux<T> delete(T e) {
			// return the list with the first occurance of e deleted
			if (this.node == e || this.node.equals(e)) {
				return this.next;
			} else {
				if (this.next != null) {
					this.next = this.next.delete(e);
					return this;
				} else {
					return this;
				}
			}
		}

		public int contain(T n) {
			if (this.next == null) {
				if (this.node == n || this.node.equals(n)) {
					return 1;
				} else {
					return 0;
				}

			} else {
				if (this.node == n || this.node.equals(n)) {
					return 1 + this.next.contain(n);
				} else {
					return this.next.contain(n);
				}
			}
		}

		public int length() {
			return length;
		}
	}

}

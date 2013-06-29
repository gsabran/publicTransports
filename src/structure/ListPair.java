package structure;

import structure.ListPair.aux;
import java.util.HashMap;

import other.p;

public class ListPair<T> {

	protected aux<T> l;
	protected HashMap<T, Integer> contain;

	public void add(T a, int n) {
		l = l == null ? new aux<T>(a, n) : l.add(a, n);
		if (contain.get(a) != null) {

			int p = contain.get(a);
			contain.put(a, p + 1);
		} else {
			contain.put(a, 1);
		}
	}

	public void remove(T a) {
		int p = contain.get(a);
		if (p > 0) {
			contain.put(a, p - 1);
			l = l.delete(a);
		}
	}

	public int get(T n) {
		if (l == null) {
			return 0;
		} else {
			return l.get(n);
		}
	}

	public boolean isEmpty() {
		return l == null;
	}

	public int contains(T a) {
		if (contain.get(a) == null) {
			return 0;
		} else {
			return contain.get(a);
		}
	}

	public Browse<Pair<T, Integer>> browse() {
		return new Browse<Pair<T, Integer>>(this.toList());
	}

	public List<Pair<T, Integer>> toList() {
		List<Pair<T, Integer>> res = new List<Pair<T, Integer>>();
		aux<T> temp = l;
		while (temp != null) {
			res.add(new Pair<T, Integer>(temp.node, temp.value));
			temp = temp.next;
		}
		return res;
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
		Browse<Pair<T, Integer>> b = this.browse();
		ListPair<T> res = new ListPair<T>();
		while (b.hasNext()) {
			Pair<T, Integer> p = b.next();
			res.add(p.x, p.y);
		}
		this.l = res.l;
	}

	public int size() {
		if (l == null) {
			return 0;
		}
		return l.length();
	}

	public ListPair() {
		contain = new HashMap<T, Integer>();
		l = null;
	}

	public ListPair(T a, int n) {
		l = new aux<T>(a, n);
		contain = new HashMap<T, Integer>();
		contain.put(a, 1);
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
		public int value;
		public aux<T> next;

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
					res = res && t1.node == t2.node && t1.value == t2.value;
					t1 = t1.next;
					t2 = t2.next;
				}

			}
			return res;
		}

		public aux() {
			node = null;
			next = null;
		}

		public aux(T n, int v) {
			node = n;
			value = v;
			next = null;
		}

		public aux<T> add(T n, int v) {
			aux<T> l = new aux<T>(n, v);
			l.next = this;
			return l;
		}

		public aux<T> delete(T e) {
			// return the ListPair with the first occurance of e deleted
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

		public int get(T n) {
			if (this.next == null) {
				if (this.node == n || this.node.equals(n)) {
					return value;
				} else {
					return 0;
				}

			} else {
				if (this.node == n || this.node.equals(n)) {
					return value;
				} else {
					return this.next.get(n);
				}
			}
		}

		public int length() {
			if (this.next == null) {
				return 1;
			} else {
				int l = this.next.length();
				return l + 1;
			}
		}
	}

}

package fanfan;

import java.util.Random;

/**
 * A Java implemetion of Cuckoo Filter
 */
public class CuckooFilter {

    static final int MAXIMUM_CAPACITY = 1 << 30;

    private final int MAX_NUM_KICKS = 500;
    private int capacity;
    private int size = 0;
    private Bucket[] buckets;
    private Random random; 
   //����CuckooFilter��
    public CuckooFilter(int capacity) {
        capacity = tableSizeFor(capacity);
        this.capacity = capacity;
        buckets = new Bucket[capacity];
        System.out.println("����Ϊ"+buckets.length);
        random = new Random();
        for (int i = 0; i < capacity; i++) {
            buckets[i] = new Bucket();
        }
    }


    /**
     * insert an object into cuckoo filter
     */
    public boolean insert(Object o) {
        if (o == null)
            return false;
        byte f = fingerprint(o);
        int i1 = hash(o);
        int i2 = i1 ^ hash(f);

        if (buckets[i1].insert(f) || buckets[i2].insert(f)) {
            size++;
            System.out.println("����"+o+"�ɹ�");
            expandArray();
            return true;
        }
        // �����ٲ���
        return relocateAndInsert(i1, i2, f);
    }
    public void expandArray() {
    	//˵��Ҫ�������ݲ�����
    if (size>=buckets.length) {
    	 Bucket[] oldbuckets =buckets;
    	 capacity=capacity*2;
    	 buckets = new Bucket[capacity];
    	 System.out.println("����ǰ�ı�"+oldbuckets.length);
    	 System.out.println("���ݺ�ı�"+buckets.length);
    	 size=0;
    	 for (int i = 0; i < capacity; i++) {
             buckets[i] = new Bucket();
         }
    	 for(int i=0;i<oldbuckets.length;i++)
    	 {
    		 for(int j=0;j<Bucket.BUCKET_SIZE;j++)
    		  
    			 if(oldbuckets[i].fps[j]!=0)
    			 {
    				 buckets[i].fps[j]=oldbuckets[i].fps[j];
    				 size++;
    			 }
    	 }
     }
    }
    

	/**
     * �ٲ���ǰ�ж�һ�¸�Ԫ���Ƿ��Ѿ�����
     */
    public boolean insertUnique(Object o) {
        if (o == null || contains(o))
            return false;
        return insert(o);
    }

     //�ٴβ���
    private boolean relocateAndInsert(int i1, int i2, byte f) {
        boolean flag = random.nextBoolean();
        int itemp = flag ? i1 : i2;
        for (int i = 0; i < MAX_NUM_KICKS; i++) {
            int position = random.nextInt(Bucket.BUCKET_SIZE);
            f = buckets[itemp].swap(position, f);
            itemp = itemp ^ hash(f);
            if (buckets[itemp].insert(f)) {
                size++;
                System.out.println("��λ����ɹ�");
                return true;
            }
        }
        System.out.println("����ʧ��");
        return false;
    }
    //���Һ���
    public boolean contains(Object o) {
        if(o == null)
            return false;
        byte f = fingerprint(o);
        int i1 = hash(o);
        int i2 = i1 ^ hash(f);
        return buckets[i1].contains(f) || buckets[i2].contains(f);
    }

    //ɾ������
    public boolean delete(Object o) {
        if(o == null)
            return false;
        byte f = fingerprint(o);
        int i1 = hash(o);
        int i2 = i1 ^ hash(f);
       if( buckets[i1].delete(f) || buckets[i2].delete(f))
       {
           System.out.println("ɾ��"+o+"�ɹ�");
           return true;
       }
       System.out.println("ɾ��"+o+"ʧ��");
       return false;
    }

    
 
    public int size() {
        return size;
    }
     //�жϱ��Ƿ�Ϊ��
    public boolean isEmpty() {
        return size == 0;
    }
    //ָ�ƺ���
    private byte fingerprint(Object o) {
        int h = o.hashCode();
        h += ~(h << 15);
        h ^= (h >> 10);
        h += (h << 3);
        h ^= (h >> 6);
        h += ~(h << 11);
        h ^= (h >> 16);
        byte hash = (byte) h;
        if (hash == Bucket.NULL_FINGERPRINT)
            hash = 40;
        return hash;
    }
    //��ϣ����
    public int hash(Object key) {
        int h = key.hashCode();
        h -= (h << 6);
        h ^= (h >> 17);
        h -= (h << 9);
        h ^= (h << 4);
        h -= (h << 3);
        h ^= (h << 10);
        h ^= (h >> 15);
        return h & (capacity - 1);
    }

    //��ϣ���С
    static final int tableSizeFor(int cap) {
        int n = cap*4;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n;
    }

    static class Bucket {
        
		public static final int FINGERPINT_SIZE = 1;
        public static final int BUCKET_SIZE = 4;
        public static final byte NULL_FINGERPRINT = 0;
        
        public final byte[] fps1 = new byte[BUCKET_SIZE];
        private final byte[] fps = new byte[BUCKET_SIZE];

        public boolean insert(byte fingerprint) {
            for (int i = 0; i < fps.length; i++) {
                if (fps[i] == NULL_FINGERPRINT) {
                    fps[i] = fingerprint;
                    return true;
                }
            }
            return false;
        }


        public boolean delete(byte fingerprint) {
            for (int i = 0; i < fps.length; i++) {
                if (fps[i] == fingerprint) {
                    fps[i] = NULL_FINGERPRINT;
                    return true;
                }
            }
            return false;
        }

        public boolean contains(byte fingerprint) {
            for (int i = 0; i < fps.length; i++) {
                if (fps[i] == fingerprint)
                    return true;
            }
            return false;
        }

        public byte swap(int position, byte fingerprint) {
            byte tmpfg = fps[position];
            fps[position] = fingerprint;
            return tmpfg;
        }
        
    }
    public static void main(String[] args) {
    	CuckooFilter cuckoofilter = new CuckooFilter(20);
		for (int i = 0; i < 100; i++) {
			cuckoofilter.insert(i);
		}
		System.out.println("sizeΪ"+cuckoofilter.size);
		for (int i = 0; i < 10; i++) {
			cuckoofilter.delete(i);
		}
	}

}
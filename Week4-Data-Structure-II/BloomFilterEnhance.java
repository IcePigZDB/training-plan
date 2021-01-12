package fanfan;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ZhouFan
 * @ClassName BloomFilterEnhance
 * @Description ��¡��������Javaʵ���࣬��ɾ������
 */
public class BloomFilterEnhance{
	
	 
	private static int  BLOOM_HASH_TABLE_SIZE;//��ϣ��Ĵ�С
	private final int[] seeds;//��ϣ�������
	private final AtomicInteger useCount = new AtomicInteger();
	private final Double autoClearRate;//�Զ���ո���
	//��¡�������Ĺ�ϣ��
    private Map<Integer, Integer> bloomFilterCountMap = new HashMap<>();
    
		//�Զ���չ������ڲ���Ϣ��ʹ�ñ��ʣ���null���ʾ�����Զ�����;
		//��������ʹ���ʴﵽ100%ʱ�������۴���ʲô���ݣ�������Ϊ�������Ѿ�������;
		//��ϣ��������ʹ���ʴﵽ80%ʱ�Զ��������ʹ�ã�����0.8
		public  BloomFilterEnhance(MisjudgmentRate rate, int dataCount, Double autoClearRate){
			
		   int  BLOOM_HASH_TABLE_SIZE = rate.seeds.length * dataCount;
		         System.out.println("��ϣ��Ϊ"+BLOOM_HASH_TABLE_SIZE);
			 if(BLOOM_HASH_TABLE_SIZE<0 || BLOOM_HASH_TABLE_SIZE>Integer.MAX_VALUE){
				throw new RuntimeException("λ��̫������ˣ��뽵�������ʻ��߽������ݴ�С");
			}
			seeds = rate.seeds;
			this.autoClearRate = autoClearRate;
			BloomFilterEnhance.BLOOM_HASH_TABLE_SIZE= BLOOM_HASH_TABLE_SIZE;
			for (int i = 0; i < BLOOM_HASH_TABLE_SIZE; i++) {
	            bloomFilterCountMap.put(i, 0);
	        }
		}
		//��ȡhashcode
		private int hash(String data, int seeds) {
			char[] value = data.toCharArray();
			int hash = 0;
			if(value.length>0){
				for(int i=0; i<value.length; i++){
					hash = hash + seeds*value[i];
				}
			}
			hash = hash % BLOOM_HASH_TABLE_SIZE;
			return Math.abs(hash);
		}
    
    public enum MisjudgmentRate {
		// ����Ҫѡȡ�������ܺܺõĽ��ʹ�����
	
		VERY_SMALL(new int[] {3}),
		
		SMALL(new int[] {15,77}), //
		
		MIDDLE(new int[] { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53 }), //
		
		HIGH(new int[] {  59, 61, 67, 71, 73, 79, 83, 89, 97,101, 103, 107, 109, 113, 127, 131 });
		
		private int[] seeds;
		
		//ö������MIDDLE���캯����seeds�����ʼ��
		private MisjudgmentRate(int[] seeds) {

			
			this.seeds = seeds;
		}
		
		public int[] getSeeds() {
			return seeds;
		}
		
		public void setSeeds(int[] seeds) {
			this.seeds = seeds;
		}
	}

    /**
     * ��¡����������������
     */
    public void add(String element) {
    	//�Ƿ���Ҫ����
    	checkNeedClear();
    	//����ÿ�ֹ�ϣ�����õ��Ĺ�ϣֵ
    	
    	for(int i=0; i<seeds.length; i++)
    	{
			//����λhashֵ
    		int bloomHashCode = hash(element, seeds[i]); 
            //���뵽��¡������"CountMap"֮��
            bloomFilterCountMap.put(bloomHashCode, bloomFilterCountMap.get(bloomHashCode) + 1);

    	}
    }
    //��չ�����
	private void checkNeedClear() {
		if(autoClearRate != null){
			if(getUseRate() >= autoClearRate){
				synchronized (this) {
					if(getUseRate() >= autoClearRate){
						bloomFilterCountMap.clear();
						useCount.set(0);
					}
				}
			}
		}
	}
	//�õ���ǰ��������ʹ����
	private Double getUseRate() {
		return (double)useCount.intValue()/(double)BLOOM_HASH_TABLE_SIZE;
	}
    /**
     * ��¡����������ѯ�Ƿ���ڣ���һ���������ʣ�����ϣ���С�͹�ϣ�����йأ�
     */
    public boolean exists(String element) {
        //����ÿ�ֹ�ϣ�����õ��Ĺ�ϣֵ
    	for(int i=0; i<seeds.length; i++)
    	{
			//����λhashֵ
    		int bloomHashCode = hash(element, seeds[i]); 
            //��ȡhashcode
            if(bloomFilterCountMap.get(bloomHashCode) <= 0) 
                  return false;
    	}
    	return true;
    	
    }
        

    /**
     * ��¡��������ɾ��ĳ��Ԫ�أ�������ԭ��add()����Ԫ�أ�������һ�����գ�
     */
    public boolean delete(String element) {
        //�����Ҫɾ����element�����ڣ�����false
        if (!exists(element)) {
            return false;
        }
      //����ÿ�ֹ�ϣ�����õ��Ĺ�ϣֵ
    	for(int i=0; i<seeds.length; i++)
    	{
			//����λhashֵ
    		int bloomHashCode = hash(element, seeds[i]); 
    		bloomFilterCountMap.put(bloomHashCode, bloomFilterCountMap.get(bloomHashCode) - 1);
    	}
        
        //ɾ���ɹ�
        return true;
    }

    /**
     * ������֤��¡������
     */
    public static void main(String[] args) {
        //��¡��������
        BloomFilterEnhance bloomFilterEnhance = new BloomFilterEnhance(MisjudgmentRate.MIDDLE,50, 0.8);
        int j=0;
        //����0-100000������
		for(int i=0;i<100000;i++) {
			bloomFilterEnhance.add(Integer.toString(i));
		}
		//����100000-200000������
		for(int i=100000;i<2000000;i++) {
			if(bloomFilterEnhance.exists(Integer.toString(i)))
				{
				System.out.println("��"+i+"������ͻ��");
				j++;
				}
		}
		System.out.println("�ܹ���"+j+"������ͻ��");
	
	}

    
}

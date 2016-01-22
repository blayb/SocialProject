/*******************************************************************************
 * Copyright 2013 Comcast Cable Communications Management, LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.test.testproject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.graphics.Rect;
import android.util.Log;

import com.comcast.freeflow.core.FreeFlowItem;
import com.comcast.freeflow.core.Section;
import com.comcast.freeflow.layouts.FreeFlowLayout;
import com.comcast.freeflow.layouts.FreeFlowLayoutBase;
import com.comcast.freeflow.utils.ViewUtils;

/*
This class implements FreeFlowLayout
 */
public class SocialLayout extends FreeFlowLayoutBase implements FreeFlowLayout {

	private static final String TAG = "SocialLayout";

	private int largeItemSide;
	private int regularItemSide;


	@Override
	public void setDimensions(int measuredWidth, int measuredHeight) {
		super.setDimensions(measuredWidth, measuredHeight);
		largeItemSide = (measuredWidth/ 3);
		regularItemSide = (measuredWidth/ 6);

	}

	private HashMap<Object, FreeFlowItem> map;
	private Section section;

	@Override
	public void prepareLayout(){
		map = new HashMap<Object, FreeFlowItem>();
		section = itemsAdapter.getSection(0);

		for (int i = 0; i < section.getDataCount(); i++) {
			int chunk = 1;

			RectImage r = (RectImage)section.getDataAtIndex(i);

			FreeFlowItem freeFlowItem = new FreeFlowItem();
			freeFlowItem.isHeader = false;
			freeFlowItem.itemIndex = i;
			freeFlowItem.itemSection = 0;
			freeFlowItem.data = section.getDataAtIndex(i);

			Rect rect = new Rect();

			//| 0 | 2 |7|8|
			//| 0 |3|4|9|10|
			//| 1 |5|6|11|12|

			// the 3 conditions is specific cases
			if(i % 13 ==  0){
				rect.left = 0;
				rect.top = chunk *r.row * largeItemSide;
				rect.right = largeItemSide;
				rect.bottom = rect.top + largeItemSide;
			}else if(i % 13 == 1){
				rect.left = 0;
				rect.top = chunk *r.row *regularItemSide;
				rect.right = largeItemSide;
				rect.bottom = rect.top + regularItemSide;
			}else if(i % 13 == 2){
				rect.left = largeItemSide;
				rect.top = chunk *r.row *largeItemSide;
				rect.right = 2*rect.left;
				rect.bottom = rect.top + regularItemSide;
			}else{

				//it's even number need to add regularItemSide to right position
				if(i%2 == 0){
					rect.left = largeItemSide*r.column + regularItemSide;
					rect.top = chunk *r.row * regularItemSide;
					rect.right = rect.left+regularItemSide;
					rect.bottom = rect.top + regularItemSide;
				}else{//is odd number
					rect.left = largeItemSide* r.column;
					rect.top = chunk *r.row * regularItemSide;
					rect.right = rect.left+regularItemSide;
					rect.bottom = rect.top + regularItemSide;
				}

			}

			freeFlowItem.frame = rect;
			map.put(section.getDataAtIndex(i), freeFlowItem);
		}
	}
	@Override
	public HashMap<Object, FreeFlowItem> getItemProxies(
			int viewPortLeft, int viewPortTop) {

		Rect viewport = new Rect(viewPortLeft, 
								viewPortTop, 
								viewPortLeft + width, 
								viewPortTop + height);
		
		//Log.d(TAG, "Viewport: "+viewPortLeft+", "+viewPortTop+", "+viewport.width()+","+viewport.height());
		HashMap<Object, FreeFlowItem> ret = new HashMap<Object, FreeFlowItem>();

		Iterator<Entry<Object, FreeFlowItem>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Object, FreeFlowItem> pairs = it.next();
			FreeFlowItem p = (FreeFlowItem) pairs.getValue();
			if ( Rect.intersects(p.frame, viewport) ) {
				ret.put(pairs.getKey(), p);
			}
		}
		return ret;
		
	}

	@Override
	public FreeFlowItem getFreeFlowItemForItem(Object item) {
		return map.get(item);
	}

	@Override
	public int getContentWidth() {
		return 0;
	}

	@Override
	public int getContentHeight() {
		return 0;
	}

	@Override
	public FreeFlowItem getItemAt(float x, float y) {
		return (FreeFlowItem) ViewUtils.getItemAt(map, (int) x, (int) y);
	}

	@Override
	public void setLayoutParams(FreeFlowLayoutParams params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean verticalScrollEnabled() {
		return true;
	}
	
	@Override
	public boolean horizontalScrollEnabled(){
		return false;
	}
}

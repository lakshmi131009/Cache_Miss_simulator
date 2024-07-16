#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <assert.h>
#include<bits/stdc++.h>


int main(int argc, char* argv[])
{
int k,i;
FILE* fp;
FILE* fp2;
char input_name[100];
char iord,type;
unsigned long long addr;
unsigned pc;
int numtraces = atoi(argv[2]);
for (k=0; k<numtraces; k++) {
      sprintf(input_name, "%s_%d", argv[1], k);
      fp = fopen(input_name, "rb");
      assert(fp != NULL);
      }
fp2= fopen("ot3.txt","w+");
      while (!feof(fp)) {
         fread(&iord, sizeof(char), 1, fp);
         fread(&type, sizeof(char), 1, fp);
         fread(&addr, sizeof(unsigned long long ), 1, fp);
         fread(&pc, sizeof(unsigned), 1, fp);

       if((int)type!=0)
    fprintf(fp2, "%lld\n",addr);

}
      fclose(fp);
      fclose(fp2);
     //text file created which has the addresses and its name is ot3.txt


return 0;


}



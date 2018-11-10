package objects;

import java.math.BigDecimal;

public class SingleGameDB {
    private int name;
    private int rank;
    private BigDecimal price;
    private int merchant;
    private byte isThisNewGame;
    private byte priceChange;
    private byte merchantChange;
    private byte rankChange;
    private byte comeBack;      // if it is not a new game but it has not been in the last check

    public byte getComeBack() {
        return comeBack;
    }

    public void setComeBack(byte comeBack) {
        this.comeBack = comeBack;
    }

    public byte getIsThisNewGame() {
        return isThisNewGame;
    }

    public void setIsThisNewGame(byte isThisNewGame) {
        this.isThisNewGame = isThisNewGame;
    }

    public byte getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(byte priceChange) {
        this.priceChange = priceChange;
    }

    public byte getMerchantChange() {
        return merchantChange;
    }

    public void setMerchantChange(byte merchantChange) {
        this.merchantChange = merchantChange;
    }

    public byte getRankChange() {
        return rankChange;
    }

    public void setRankChange(byte rankChange) {
        this.rankChange = rankChange;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getMerchant() {
        return merchant;
    }

    public void setMerchant(int merchant) {
        this.merchant = merchant;
    }

}
